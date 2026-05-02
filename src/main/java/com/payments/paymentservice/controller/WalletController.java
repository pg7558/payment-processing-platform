package com.payments.paymentservice.controller;

import com.payments.paymentservice.dto.*;
import com.payments.paymentservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody CreateWalletRequest request) {
        return ResponseEntity.ok(walletService.createWallet(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/add-money")
    public ResponseEntity<String> addMoney(@RequestBody AddMoneyRequest request) {
        walletService.addMoney(request);
        return ResponseEntity.ok("Money added successfully");
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletService.transfer(request));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getTransactions(userId));
    }
}