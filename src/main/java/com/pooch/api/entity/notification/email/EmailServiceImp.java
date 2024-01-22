package com.pooch.api.entity.notification.email;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pooch.api.entity.demo.Demo;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.subscriber.Subscriber;
import com.pooch.api.entity.notification.email.template.EmailTemplate;
import com.pooch.api.entity.parent.Parent;
import com.sun.mail.smtp.SMTPSendFailedException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImp implements EmailService {

    // @Autowired
    // private AwsEmailService awsEmailService;

    @Autowired
    private EmailDAO           emailDAO;

    @Value("${project.web.app.uri}")
    private String             appHost;

    @Autowired
    private EmailSenderService emailSenderService;

    // @Autowired
    // private EmailTemplateDAO emailTemplateDAO;

    @Override
    public void send(Groomer groomer, EmailTemplate emailTemplate, Map<String, Object> data) {
        log.info("send");

        if (emailTemplate == null) {
            return;
        }

        log.info("emailTemplate={}", emailTemplate.toJson());

        Email email = new Email();
        email.setSubject(emailTemplate.getSubject());
        email.setSendTo(groomer.getEmail().toLowerCase());
        email.setTemplateUuid(emailTemplate.getUuid());
        email.setStatus(EmailStatus.SENDING);

        String content = EmailUtils.subValues(data, emailTemplate.getContent());

        email.setContent(content);

        email.setDynamicData(data);
        email.setEmailTemplateId(emailTemplate.getSenderTemplateId());

        try {

            DeliveryStatus status = emailSenderService.sendEmail(email, emailTemplate.getSendToUser());

            if (status.isDelivered()) {

                email.setStatus(EmailStatus.SENT);
            } else {
                email.setStatus(EmailStatus.ERROR_SENT);

                email.setError(status.getMessage());
            }
        } catch (SMTPSendFailedException e) {
            log.warn("SMTPSendFailedException, msg={}", e.getLocalizedMessage());
        }

        emailDAO.save(email);
    }

    @Override
    public void send(Parent parent, EmailTemplate emailTemplate, Map<String, Object> data) {
        log.info("send");

        if (emailTemplate == null) {
            return;
        }

        log.info("emailTemplate={}", emailTemplate.toJson());

        Email email = new Email();
        email.setSubject(emailTemplate.getSubject());
        email.setSendTo(parent.getEmail().toLowerCase());
        email.setTemplateUuid(emailTemplate.getUuid());
        email.setStatus(EmailStatus.SENDING);
        String content = EmailUtils.subValues(data, emailTemplate.getContent());

        email.setContent(content);

        try {
            DeliveryStatus status = emailSenderService.sendEmail(email, emailTemplate.getSendToUser());

            if (status.isDelivered()) {

                email.setStatus(EmailStatus.SENT);
            } else {
                email.setStatus(EmailStatus.ERROR_SENT);

                email.setError(status.getMessage());
            }
        } catch (SMTPSendFailedException e) {
            log.warn("SMTPSendFailedException, msg={}", e.getLocalizedMessage());
        }

        emailDAO.save(email);
    }

    @Override
    public void send(Demo demo, EmailTemplate emailTemplate, Map<String, Object> data) {

        Email email = new Email();
        email.setSendTo(demo.getEmail().toLowerCase());
        email.setStatus(EmailStatus.SENDING);
        email.setSubject(emailTemplate.getSubject());
        email.setDynamicData(data);
        email.setTemplateUuid(emailTemplate.getUuid());
        email.setEmailTemplateId(emailTemplate.getSenderTemplateId());

        try {

            DeliveryStatus status = emailSenderService.sendEmail(email, emailTemplate.getSendToUser());

            if (status.isDelivered()) {

                email.setStatus(EmailStatus.SENT);
            } else {
                email.setStatus(EmailStatus.ERROR_SENT);

                email.setError(status.getMessage());
            }
        } catch (SMTPSendFailedException e) {
            log.warn("SMTPSendFailedException, msg={}", e.getLocalizedMessage());
        }

        emailDAO.save(email);
    }

    @Override
    public void send(Subscriber subscriber, EmailTemplate emailTemplate, Map<String, Object> data) {
        Email email = new Email();
        email.setSendTo(subscriber.getEmail().toLowerCase());
        email.setStatus(EmailStatus.SENDING);
        email.setSubject(emailTemplate.getSubject());
        email.setDynamicData(data);
        email.setTemplateUuid(emailTemplate.getUuid());
        email.setEmailTemplateId(emailTemplate.getSenderTemplateId());

        try {

            DeliveryStatus status = emailSenderService.sendEmail(email, emailTemplate.getSendToUser());

            if (status.isDelivered()) {

                email.setStatus(EmailStatus.SENT);
            } else {
                email.setStatus(EmailStatus.ERROR_SENT);

                email.setError(status.getMessage());
            }
        } catch (SMTPSendFailedException e) {
            log.warn("SMTPSendFailedException, msg={}", e.getLocalizedMessage());
        }

        emailDAO.save(email);
    }

    @Override
    public void send(Email email, EmailTemplate emailTemplate, Map<String, Object> data) {
        // TODO Auto-generated method stub
        try {

            DeliveryStatus status = emailSenderService.sendEmail(email, emailTemplate.getSendToUser());

            if (status.isDelivered()) {

                email.setStatus(EmailStatus.SENT);
            } else {
                email.setStatus(EmailStatus.ERROR_SENT);

                email.setError(status.getMessage());
            }
        } catch (SMTPSendFailedException e) {
            log.warn("SMTPSendFailedException, msg={}", e.getLocalizedMessage());
        }

        emailDAO.save(email);
    }

}
