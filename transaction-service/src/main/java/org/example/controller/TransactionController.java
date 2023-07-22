package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.dto.CreateTransactionRequest;
import org.example.model.Transaction;
import org.example.model.User;
import org.example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public String transact(@RequestBody @Valid CreateTransactionRequest createTransactionRequest) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User sender = (User) authentication.getPrincipal();
        Transaction transaction = createTransactionRequest.toTransaction();
        transaction.setSenderId(sender.getUsername());
        return transactionService.transact(transaction);
    }
}
