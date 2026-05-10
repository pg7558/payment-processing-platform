package com.payments.paymentservice.service;

import com.payments.paymentservice.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendPayment(PaymentEvent event) {

        kafkaTemplate.send("payment-events", event)
                .whenComplete((result, ex) -> {

                    if (ex != null) {
                        log.error("Failed to send Kafka event", ex);
                    } else {
                        log.info("Kafka event sent successfully");
                    }
                });
    }
}