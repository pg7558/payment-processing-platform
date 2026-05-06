package com.notifications.notifications_service.service;

import com.notifications.notifications_service.dto.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {


    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void consume(PaymentEvent event){
        System.out.println("Received Payment event: "+event);

        System.out.println("Notifying user : "+event.getToUser());
    }

}
