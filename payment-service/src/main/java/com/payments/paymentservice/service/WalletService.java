package com.payments.paymentservice.service;

import com.payments.paymentservice.dto.*;
import com.payments.paymentservice.entity.Transaction;
import com.payments.paymentservice.entity.Wallet;
import com.payments.paymentservice.exception.BadRequestException;
import com.payments.paymentservice.exception.NotFoundException;
import com.payments.paymentservice.repository.TransactionRepository;
import com.payments.paymentservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final PaymentProducer paymentProducer;

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

    @Transactional(timeout = 5)
    public void addMoney(AddMoneyRequest request) {

        if (request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + request.getAmount());

        walletRepository.save(wallet);

        log.info("Money added successfully for user {}", request.getUserId());
    }

    @Transactional(timeout = 5)
    public String transfer(TransferRequest request) {

        long start = System.currentTimeMillis();

        log.info("Transfer started");

        if (request.getIdempotencyKey() == null) {
            throw new BadRequestException("Idempotency key is required");
        }

        Optional<Transaction> existingTxn =
                transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existingTxn.isPresent()) {
            return "Duplicate request ignored. Previous status: "
                    + existingTxn.get().getStatus();
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

            // Prevent deadlocks by locking in consistent order
            Long first = Math.min(request.getFromUser(), request.getToUser());
            Long second = Math.max(request.getFromUser(), request.getToUser());

            Wallet firstWallet = walletRepository.findByUserIdForUpdate(first)
                    .orElseThrow(() -> new NotFoundException("Wallet not found"));

            Wallet secondWallet = walletRepository.findByUserIdForUpdate(second)
                    .orElseThrow(() -> new NotFoundException("Wallet not found"));

            Wallet fromWallet =
                    first.equals(request.getFromUser()) ? firstWallet : secondWallet;

            Wallet toWallet =
                    first.equals(request.getFromUser()) ? secondWallet : firstWallet;

            if (fromWallet.getBalance() < request.getAmount()) {
                throw new BadRequestException("Insufficient balance");
            }

            // Debit sender
            fromWallet.setBalance(
                    fromWallet.getBalance() - request.getAmount()
            );

            // Credit receiver
            toWallet.setBalance(
                    toWallet.getBalance() + request.getAmount()
            );

            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            txn.setStatus("SUCCESS");

            // Save transaction FIRST
            transactionService.saveTransaction(txn);

            // Send Kafka event ASYNC
            paymentProducer.sendPayment(
                    new PaymentEvent(
                            request.getFromUser(),
                            request.getToUser(),
                            request.getAmount(),
                            "SUCCESS",
                            LocalDateTime.now()
                    )
            );

            log.info(
                    "Transfer completed in {} ms",
                    System.currentTimeMillis() - start
            );

            return "Transfer successful";

        } catch (Exception ex) {

            txn.setStatus("FAILED");

            transactionService.saveTransaction(txn);

            log.error("Transfer failed", ex);

            throw ex;
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