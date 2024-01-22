package com.pooch.api.entity.notification.email;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.utils.ObjectUtils;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.sun.mail.smtp.SMTPSendFailedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailSenderServiceImp implements EmailSenderService {

    @Autowired
    private SendGrid sendGrid;

    private String   poochappSender     = "no-reply@poochapp.com";

    private String   poochfolioSender   = "no-reply@poochfolio.com";

    private String   poochappPersonal   = "PoochApp";

    private String   poochfolioPersonal = "Poochfolio";

    @Value("${spring.profiles.active}")
    private String   env;

    /**
     * Send Mail subject,body, and recipient email
     * 
     * Retry only when there is a throttling issue (SMTPSendFailedException)
     */
    @Retryable(value = {RuntimeException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    @Override
    public DeliveryStatus sendEmail(Email email) throws SMTPSendFailedException {
        // TODO Auto-generated method stub
        return sendEmail(email, null);
    }

    @Retryable(value = {RuntimeException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    @Override
    public DeliveryStatus sendEmail(Email email, UserType userType) throws SMTPSendFailedException {
        log.debug("Sending email");

        if (env.equalsIgnoreCase("github")) {
            return new DeliveryStatus(false, DeliveryStatus.NOT_DELIVERED);
        }

        String sendTo = null;
        String subject = null;
        // String body = null;
        String sender = null;
        String personal = null;

        if (email.getSendTo() != null && email.getSendTo().isEmpty() == false) {

            sendTo = email.getSendTo();

        } else {

            return new DeliveryStatus(true, "sendTo email is empty");
        }

        try {

            subject = email.getSubject();

        } catch (Exception e) {
            log.warn("email subject issue, to {}, reason={}", sendTo, e.getLocalizedMessage());
            return new DeliveryStatus(false, e.getLocalizedMessage());
        }

        if (userType != null && userType.equals(UserType.groomer)) {
            personal = this.poochfolioPersonal;
            sender = this.poochfolioSender;
        } else {
            personal = this.poochappPersonal;
            sender = this.poochappSender;
        }

        // try {
        //
        // body = email.getContent();
        //
        // } catch (Exception e) {
        // log.warn("email content issue, {} to {}, reason={}", subject, sendTo, e.getLocalizedMessage());
        // return new DeliveryStatus(false, e.getLocalizedMessage());
        // }

        Request request = null;

        try {

            com.sendgrid.helpers.mail.objects.Email from = new com.sendgrid.helpers.mail.objects.Email(sender, personal);

            com.sendgrid.helpers.mail.objects.Email to = new com.sendgrid.helpers.mail.objects.Email(sendTo);

            // Mail mail = new Mail(from, subject, to, new Content("text/html", body));
            Mail mail = new Mail();
            mail.setFrom(from);
            mail.setSubject(subject);

            if (email.getEmailTemplateId() != null) {
                mail.setTemplateId(email.getEmailTemplateId());
            }

            Personalization personalization = new Personalization();
            personalization.setSubject(subject);
            personalization.addTo(to);
            personalization.setFrom(from);

            if (email.getCarbonCopies() != null && !email.getCarbonCopies().isEmpty()) {
                for (String cc : email.getCarbonCopies()) {
                    personalization.addCc(new com.sendgrid.helpers.mail.objects.Email(cc));
                }
            }
            
            if (email.getBlindCarbonCopies() != null && !email.getBlindCarbonCopies().isEmpty()) {
                for (String bcc : email.getBlindCarbonCopies()) {
                    personalization.addBcc(new com.sendgrid.helpers.mail.objects.Email(bcc));
                }
            }

            Map<String, Object> dynamicData = email.getDynamicData();

            log.info("dynamicData={}", dynamicData);

            if (dynamicData != null) {

                for (String key : dynamicData.keySet()) {
                    personalization.addDynamicTemplateData(key, dynamicData.getOrDefault(key, ""));
                }
            }
            
            log.info("personalization={}", ObjectUtils.toJson(personalization));

            mail.addPersonalization(personalization);

            request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

        } catch (Exception e) {
            log.warn("Unable to send {} to {}, reason={}", subject, sendTo, e.getLocalizedMessage());
            email.setError("Exception, msg=" + e.getLocalizedMessage());
            return new DeliveryStatus(false, e.getLocalizedMessage());
        }

        Response response = null;
        log.info("sending email to sendGrid...");
        try {
            response = this.sendGrid.api(request);
            int statusCode = response.getStatusCode();
            log.info("sendGrid response, statusCode:{}, body:{}", statusCode, response.getBody());
        } catch (IOException e) {
            log.warn("IOException sending sendGrid request, msg={}", e.getLocalizedMessage());
            return new DeliveryStatus(false, e.getLocalizedMessage());
        }

        try {
            int statusCode = response.getStatusCode();

            if (statusCode >= 200 && statusCode <= 299) {
                // no error
                email.setError(null);

                log.info("`{}` sent to {}", subject, sendTo);

                return new DeliveryStatus(true, DeliveryStatus.DELIVERED);
            }

            throw new RuntimeException("email send status=failed");

        } catch (Exception e) {
            log.warn("IOException handling sendGrid response, msg={}", e.getLocalizedMessage());
            return new DeliveryStatus(false, e.getLocalizedMessage());
        }

    }

}
