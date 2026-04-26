package com.payments.paymentservice.repository;

import com.payments.paymentservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Optional<Wallet> findUserById(Long userId);
}
