package com.payments.paymentservice.config;

import com.payments.paymentservice.dto.PaymentEvent;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @PostConstruct
    public void init() {
        System.out.println("✅ KafkaConfig Loaded");
    }

    @Bean
    public NewTopic paymentTopic() {
        return new NewTopic("payment-events", 1, (short) 1);
    }
}