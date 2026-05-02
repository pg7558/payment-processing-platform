package com.payments.paymentservice.service;

import com.payments.paymentservice.entity.Transaction;
import com.payments.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTransaction(Transaction txn) {
        transactionRepository.save(txn);
    }
}