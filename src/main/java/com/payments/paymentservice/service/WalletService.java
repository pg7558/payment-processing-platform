package com.payments.paymentservice.service;

import com.payments.paymentservice.dto.CreateWalletRequest;
import com.payments.paymentservice.dto.WalletResponse;
import com.payments.paymentservice.entity.Wallet;
import com.payments.paymentservice.exception.BadRequestException;
import com.payments.paymentservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletResponse createWallet(CreateWalletRequest request){
        walletRepository.findUserById(request.getUserId())
                .ifPresent(w->{
                    throw new BadRequestException("Wallet already exists");
                });
        Wallet wallet = new Wallet();
        wallet.setUserId(request.getUserId());
        wallet.setBalance(0.0);
        walletRepository.save(wallet);

        return new WalletResponse(wallet.getUserId(), wallet.getBalance());
    }

    public WalletResponse getBalance(Long userId){
        Wallet wallet = walletRepository.findUserById(userId)
                .orElseThrow(()->new RuntimeException("Wallet not found"));

        return new WalletResponse(wallet.getUserId(),wallet.getBalance());
    }
}