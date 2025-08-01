package com.ing.core.services.repository;

import com.ing.core.services.data.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, Long> {
}
