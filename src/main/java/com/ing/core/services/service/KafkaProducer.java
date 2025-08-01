package com.ing.core.services.service;

import com.ing.core.services.data.Audit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, Audit> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Audit> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Audit auditData) {
        kafkaTemplate.send(topic, auditData);
    }
}
