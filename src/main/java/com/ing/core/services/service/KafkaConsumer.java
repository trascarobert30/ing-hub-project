package com.ing.core.services.service;

import com.ing.core.services.data.Audit;
import com.ing.core.services.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final AuditRepository auditRepository;

    @KafkaListener(topics = "AUDIT-IN", groupId = "audit-group")
    public void consumeMessage(Audit audit) {
        log.info("Received audit, event: {}", audit.getEvent());
        auditRepository.save(audit);
    }
}
