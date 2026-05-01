package com.payments.paymentservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUser;
    private Long toUser;

    private Double amount;

    private String status;

    @Column(unique = true)
    private String idempotencyKey;

    private LocalDateTime createdAt;
}
