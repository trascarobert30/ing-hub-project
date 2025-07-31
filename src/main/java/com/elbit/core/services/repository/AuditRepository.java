package com.elbit.core.services.repository;

import com.elbit.core.services.data.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, Long> {
}
