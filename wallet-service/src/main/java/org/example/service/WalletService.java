package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.Constants;
import org.example.model.Wallet;
import org.example.repository.WalletRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Value("${wallet.initial.balance}")
    Long balance;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = {Constants.USER_CREATION_TOPIC},groupId = "jbdl50")
    public void createWallet(String msg) throws ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(msg);
        String walletId = (String) obj.get("phone");

        Wallet wallet = Wallet.builder()
                .walletId(walletId)
                .currency("INR")
                .balance(balance)
                .build();
        walletRepository.save(wallet);

        // it is a good practise to publish an event after every major step in all services
    }

    @KafkaListener(topics = {Constants.TRANSACTION_CREATION_TOPIC},groupId = "Transaction")
    public void updateWallets(String msg) throws ParseException, JsonProcessingException {

        JSONObject obj = (JSONObject) new JSONParser().parse(msg);
        String senderWalletId = (String) obj.get("senderId");
        Long transactionAmount = (Long) obj.get("transactionAmount");
        String receiverWalletId = (String) obj.get("receiverId");
        String transactionId = (String) obj.get("transactionId");

        JSONObject walletObj = new JSONObject();
        walletObj.put("transactionId",transactionId);
        walletObj.put("senderWalletId",senderWalletId);
        walletObj.put("receiverWalletId",receiverWalletId);
        walletObj.put("transactionAmount",transactionAmount);

        try {
            Wallet senderWallet = walletRepository.findByWalletId(senderWalletId);
            Wallet receiverWallet = walletRepository.findByWalletId(receiverWalletId);

            if (senderWallet == null || receiverWallet == null || senderWallet.getBalance() < transactionAmount) {

                walletObj.put("senderWalletBalance", senderWallet == null ? 0 : senderWallet.getBalance());
                walletObj.put("status", "FAILED");
                kafkaTemplate.send(Constants.WALLET_UPDATED_TOPIC,
                        this.objectMapper.writeValueAsString(walletObj));
                return;
            }

            senderWallet.setBalance(senderWallet.getBalance() - transactionAmount);
            walletRepository.save(senderWallet);

            receiverWallet.setBalance(receiverWallet.getBalance() + transactionAmount);
            walletRepository.save(receiverWallet);

            walletObj.put("status", "COMPLETED");
            kafkaTemplate.send(Constants.WALLET_UPDATED_TOPIC,
                    this.objectMapper.writeValueAsString(walletObj));

        } catch (Exception e){
                walletObj.put("status", "FAILED");
                walletObj.put("errorMsg", e.getMessage());

                kafkaTemplate.send(Constants.WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(walletObj));
        }

        // it is a good practise to publish an event after every major step in all services
    }
}
