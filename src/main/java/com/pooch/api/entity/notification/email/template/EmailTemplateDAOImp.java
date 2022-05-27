package com.pooch.api.entity.notification.email.template;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class EmailTemplateDAOImp implements EmailTemplateDAO {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Override
    public EmailTemplate save(EmailTemplate emailTemplate) {
        return emailTemplateRepository.saveAndFlush(emailTemplate);
    }

    @Override
    public Optional<EmailTemplate> getByUuid(EmailTemplateUuid uuid) {
        return emailTemplateRepository.findByUuid(uuid);
    }

}
