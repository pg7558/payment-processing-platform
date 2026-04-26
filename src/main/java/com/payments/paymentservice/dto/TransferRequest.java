package com.payments.paymentservice.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Long fromUser;
    private Long toUser;
    private Double amount;
}
