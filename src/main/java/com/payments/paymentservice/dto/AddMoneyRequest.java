package com.payments.paymentservice.dto;

import lombok.Data;

@Data
public class AddMoneyRequest {
    private Long userId;
    private Double amount;
}
