package com.pooch.api.entity.notification.email.template;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    Optional<EmailTemplate> findByUuid(EmailTemplateUuid uuid);
}
