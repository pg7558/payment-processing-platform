package com.payments.paymentservice.service;

import com.payments.paymentservice.entity.Transaction;
import com.payments.paymentservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveTransaction(Transaction txn){
        transactionRepository.save(txn);
    }

}
