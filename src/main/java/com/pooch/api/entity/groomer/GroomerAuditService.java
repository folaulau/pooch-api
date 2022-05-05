package com.pooch.api.entity.groomer;

import org.springframework.scheduling.annotation.Async;

/**
 * Audit Groomer to where he should be in terms of status
 */
public interface GroomerAuditService {

    Groomer audit(Groomer groomer);

    @Async
    void auditAsync(Groomer groomer);
}
