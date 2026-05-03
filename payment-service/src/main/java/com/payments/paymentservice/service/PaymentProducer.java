package com.payments.paymentservice.service;

import com.payments.paymentservice.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendPayment(PaymentEvent event) {
        kafkaTemplate.send("payment-events", event);
    }
}