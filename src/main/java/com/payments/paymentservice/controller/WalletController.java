package com.payments.paymentservice.controller;

import com.payments.paymentservice.dto.CreateWalletRequest;
import com.payments.paymentservice.dto.WalletResponse;
import com.payments.paymentservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallets")
public class WalletController {
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody CreateWalletRequest request){
        return ResponseEntity.ok(walletService.createWallet(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getBalance(@PathVariable Long userId){
        return ResponseEntity.ok(walletService.getBalance(userId));
    }
}
