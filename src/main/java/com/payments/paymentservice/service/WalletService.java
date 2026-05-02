package com.payments.paymentservice.service;

import com.payments.paymentservice.dto.*;
import com.payments.paymentservice.entity.Transaction;
import com.payments.paymentservice.entity.Wallet;
import com.payments.paymentservice.exception.BadRequestException;
import com.payments.paymentservice.exception.NotFoundException;
import com.payments.paymentservice.repository.TransactionRepository;
import com.payments.paymentservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public WalletResponse createWallet(CreateWalletRequest request) {

        walletRepository.findByUserId(request.getUserId())
                .ifPresent(w -> {
                    throw new BadRequestException("Wallet already exists");
                });

        Wallet wallet = new Wallet();
        wallet.setUserId(request.getUserId());
        wallet.setBalance(0.0);

        walletRepository.save(wallet);

        return new WalletResponse(wallet.getUserId(), wallet.getBalance());
    }

    public WalletResponse getBalance(Long userId) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        return new WalletResponse(wallet.getUserId(), wallet.getBalance());
    }

    @Transactional
    public void addMoney(AddMoneyRequest request) {

        if (request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        Wallet wallet = walletRepository.findByUserIdForUpdate(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + request.getAmount());

        walletRepository.save(wallet);
    }

    @Transactional
    public String transfer(TransferRequest request) {

        if (request.getIdempotencyKey() == null) {
            throw new BadRequestException("Idempotency key is required");
        }

        Optional<Transaction> existingTxn =
                transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existingTxn.isPresent()) {
            return "Duplicate request ignored. Previous status: " + existingTxn.get().getStatus();
        }

        Transaction txn = new Transaction();
        txn.setFromUser(request.getFromUser());
        txn.setToUser(request.getToUser());
        txn.setAmount(request.getAmount());
        txn.setCreatedAt(LocalDateTime.now());
        txn.setIdempotencyKey(request.getIdempotencyKey());

        try {

            if (request.getAmount() <= 0) {
                throw new BadRequestException("Amount must be greater than 0");
            }

            if (request.getFromUser().equals(request.getToUser())) {
                throw new BadRequestException("Cannot transfer to same user");
            }

            // Lock in consistent order
            Long first = Math.min(request.getFromUser(), request.getToUser());
            Long second = Math.max(request.getFromUser(), request.getToUser());

            Wallet firstWallet = walletRepository.findByUserIdForUpdate(first)
                    .orElseThrow(() -> new NotFoundException("Wallet not found"));

            Wallet secondWallet = walletRepository.findByUserIdForUpdate(second)
                    .orElseThrow(() -> new NotFoundException("Wallet not found"));

            Wallet fromWallet = first.equals(request.getFromUser()) ? firstWallet : secondWallet;
            Wallet toWallet = first.equals(request.getFromUser()) ? secondWallet : firstWallet;

            if (fromWallet.getBalance() < request.getAmount()) {
                throw new BadRequestException("Insufficient balance");
            }

            // Debit & Credit
            fromWallet.setBalance(fromWallet.getBalance() - request.getAmount());
            toWallet.setBalance(toWallet.getBalance() + request.getAmount());

            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            txn.setStatus("SUCCESS");

            return "Transfer successful";

        } catch (Exception ex) {

            txn.setStatus("FAILED");
            transactionService.saveTransaction(txn);

            throw ex;

        } finally {

            if ("SUCCESS".equals(txn.getStatus())) {
                transactionService.saveTransaction(txn);
            }
        }
    }

    public List<TransactionResponse> getTransactions(Long userId) {

        List<Transaction> txns =
                transactionRepository.findByFromUserOrToUser(userId, userId);

        return txns.stream()
                .map(t -> new TransactionResponse(
                        t.getFromUser(),
                        t.getToUser(),
                        t.getAmount(),
                        t.getStatus(),
                        t.getCreatedAt()
                ))
                .toList();
    }
}