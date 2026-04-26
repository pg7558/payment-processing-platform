package com.payments.paymentservice.service;

import com.payments.paymentservice.dto.AddMoneyRequest;
import com.payments.paymentservice.dto.CreateWalletRequest;
import com.payments.paymentservice.dto.TransferRequest;
import com.payments.paymentservice.dto.WalletResponse;
import com.payments.paymentservice.entity.Wallet;
import com.payments.paymentservice.exception.BadRequestException;
import com.payments.paymentservice.exception.NotFoundException;
import com.payments.paymentservice.repository.WalletRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void addMoney(AddMoneyRequest request){
        if(request.getAmount()<=0){
            throw new BadRequestException("Amount must be greater than 0");
        }

        Wallet wallet = walletRepository.findUserById(request.getUserId())
                .orElseThrow(()-> new NotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance()+request.getAmount());
        walletRepository.save(wallet);
    }

    @Transactional
    public void transfer(TransferRequest request) {
        if (request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        if (request.getFromUser().equals(request.getToUser())) {
            throw new BadRequestException("Cannot transfer to same user");
        }

        Wallet fromWallet = walletRepository.findUserById(request.getFromUser())
                .orElseThrow(() -> new NotFoundException("Sender wallet not found"));

        Wallet toWallet = walletRepository.findUserById(request.getToUser())
                .orElseThrow(() -> new NotFoundException("Receiver wallet not found"));

        if (fromWallet.getBalance() < request.getAmount()) {
            throw new BadRequestException("Insufficient balance");
        }

        fromWallet.setBalance(fromWallet.getBalance() - request.getAmount());
        toWallet.setBalance(toWallet.getBalance() + request.getAmount());

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);
    }
}