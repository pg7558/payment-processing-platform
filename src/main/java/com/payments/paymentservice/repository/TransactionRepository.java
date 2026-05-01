package com.payments.paymentservice.repository;

import com.payments.paymentservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByFromUserOrToUser(Long fromUser,Long toUser);

}
