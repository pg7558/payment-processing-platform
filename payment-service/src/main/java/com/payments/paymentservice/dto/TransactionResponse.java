package com.payments.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private Long fromUser;
    private Long toUser;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
}
