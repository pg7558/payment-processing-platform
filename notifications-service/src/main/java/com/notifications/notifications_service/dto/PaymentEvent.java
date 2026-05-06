package com.notifications.notifications_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {

    private Long fromUser;
    private Long toUser;
    private Double amount;
    private String status;
    private LocalDateTime timeStamp;

}
