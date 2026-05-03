package com.payments.paymentservice.service;

import com.payments.paymentservice.dto.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    @KafkaListener(topics = "payment-events", groupId = "test-group")
    public void consume(PaymentEvent event) {
        System.out.println("🔥 Received: " + event);
    }
}