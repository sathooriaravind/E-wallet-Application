package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.Constants;
import org.example.dto.GetUserResponse;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.repository.TransactionRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private RestTemplate restTemplate = new RestTemplate();

    private static final String TRANSACTION_COMPLETED = "COMPLETED";

    private static final String TRANSACTION_FAILED = "FAILED";


    public String transact(Transaction transaction) throws JsonProcessingException {

        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setTransactionId(UUID.randomUUID().toString());
        transactionRepository.save(transaction);

        JSONObject transactionObj = new JSONObject();
        transactionObj.put("senderId",transaction.getSenderId());
        transactionObj.put("receiverId",transaction.getReceiverId());
        transactionObj.put("transactionAmount",transaction.getTransactionAmount());
        transactionObj.put("transactionId",transaction.getTransactionId());
        // useful in publishing an event in return to this service by wallet service


        kafkaTemplate.send(Constants.TRANSACTION_CREATED_TOPIC,
                this.objectMapper.writeValueAsString(transactionObj));

        return transaction.getTransactionId();
    }

    @KafkaListener(topics = {Constants.WALLET_UPDATED_TOPIC},groupId = "wallet")
    public void updateTransaction(String msg) throws ParseException, JsonProcessingException {
        JSONObject obj = (JSONObject) new JSONParser().parse(msg);
        String senderWalletId = (String) obj.get("senderWalletId");
        String receiverWalletId = (String) obj.get("receiverWalletId");
        String transactionId = (String) obj.get("transactionId");
        String walletUpdatedStatus = (String) obj.get("status");
        Long transactionAmount = (Long) obj.get("transactionAmount");

        Transaction transaction = transactionRepository.findByTransactionId(transactionId);
        TransactionStatus transactionStatus;

        if(walletUpdatedStatus.equals(TRANSACTION_COMPLETED)){
            transactionStatus = TransactionStatus.COMPLETED;
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        }
        else{
            transactionStatus = TransactionStatus.FAILED;
            transaction.setTransactionStatus(TransactionStatus.FAILED);
        }
        transactionRepository.save(transaction);

        /**
         *  To get sender and receiver details using authentication details of this service as user
         */

        HttpHeaders httpHeaders = new HttpHeaders();

        String app_username = Constants.this_service_username;
        String password = Constants.this_service_password;
        String plainCredentials = app_username + ":" + password;
        String base64Credentials = Base64.getEncoder().encodeToString(plainCredentials.getBytes());

        httpHeaders.add("Authorization", "Basic " + base64Credentials);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<GetUserResponse> responseEntity = restTemplate.exchange("http://localhost:9000/user/phone/" +
                senderWalletId, HttpMethod.GET, requestEntity, GetUserResponse.class);

        GetUserResponse responseBody = responseEntity.getBody();

        String senderEmail = responseBody.getEmail();

        responseEntity = restTemplate.exchange("http://localhost:9000/user/phone/" +
                receiverWalletId, HttpMethod.GET, requestEntity, GetUserResponse.class);

        responseBody = responseEntity.getBody();

        String receiverEmail = responseBody.getEmail();

        obj = new JSONObject();

        obj.put("senderId",senderWalletId);
        obj.put("receiverId",receiverWalletId);
        obj.put("senderEmail",senderEmail);
        obj.put("receiverEmail",receiverEmail);
        obj.put("transactionAmount", transactionAmount);
        obj.put("transactionStatus",transactionStatus.toString());
        obj.put("transactionId",transactionId);

        kafkaTemplate.send(Constants.TRANSACTION_COMPLETED_TOPIC,this.objectMapper.writeValueAsString(obj));
    }
}
