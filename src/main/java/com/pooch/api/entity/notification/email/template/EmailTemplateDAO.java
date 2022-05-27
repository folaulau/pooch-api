package com.pooch.api.entity.notification.email.template;

import java.util.Optional;

public interface EmailTemplateDAO {

    EmailTemplate save(EmailTemplate emailTemplate);

    Optional<EmailTemplate> getByUuid(EmailTemplateUuid uuid);
}
