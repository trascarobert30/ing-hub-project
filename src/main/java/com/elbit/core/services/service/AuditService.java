package com.elbit.core.services.service;

import com.elbit.core.services.data.Audit;
import com.elbit.core.services.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;

    public List<Audit> getAllAudits() {
        return auditRepository.findAll();
    }
}
