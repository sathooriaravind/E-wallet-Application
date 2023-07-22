package org.example.controller;

import org.example.dto.UpdateWalletRequest;
import org.example.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class WalletController {

    @Autowired
    WalletService walletService;

    @PostMapping("/wallet/{id}")
    public void updateWallet(@RequestBody @Valid UpdateWalletRequest updateWalletRequest){
        // later we take id directly from security context
    }

}
