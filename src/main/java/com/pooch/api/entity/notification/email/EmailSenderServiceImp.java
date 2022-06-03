package com.pooch.api.entity.notification.email;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.pooch.api.entity.role.UserType;
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

  private String poochappSender = "no-reply@poochapp.com";

  private String poochfolioSender = "no-reply@poochfolio.com";

  private String poochappPersonal = "PoochApp";

  private String poochfolioPersonal = "Poochfolio";

  @Value("${spring.profiles.active}")
  private String env;

  /**
   * Send Mail subject,body, and recipient email
   * 
   * Retry only when there is a throttling issue (SMTPSendFailedException)
   */
  @Retryable(value = {RuntimeException.class}, maxAttempts = 3,
      backoff = @Backoff(delay = 2000, multiplier = 2))
  @Override
  public DeliveryStatus sendEmail(Email email) throws SMTPSendFailedException {
    // TODO Auto-generated method stub
    return sendEmail(email, null);
  }

  @Retryable(value = {RuntimeException.class}, maxAttempts = 3,
      backoff = @Backoff(delay = 2000, multiplier = 2))
  @Override
  public DeliveryStatus sendEmail(Email email, UserType userType) throws SMTPSendFailedException {
    log.debug("Sending email");

    if (env.equalsIgnoreCase("github")) {
      return new DeliveryStatus(false, DeliveryStatus.NOT_DELIVERED);
    }

    String sendTo = null;
    String subject = null;
    String body = null;
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

    try {

      body = email.getContent();

    } catch (Exception e) {
      log.warn("email content issue, {} to {}, reason={}", subject, sendTo,
          e.getLocalizedMessage());
      return new DeliveryStatus(false, e.getLocalizedMessage());
    }

    Request request = null;

    try {

      com.sendgrid.helpers.mail.objects.Email from =
          new com.sendgrid.helpers.mail.objects.Email(sender, personal);

      com.sendgrid.helpers.mail.objects.Email to =
          new com.sendgrid.helpers.mail.objects.Email(sendTo);

      Mail mail = new Mail(from, subject, to, new Content("text/html", body));

      Personalization personalization = new Personalization();
      personalization.setSubject(subject);
      personalization.addTo(to);
      personalization.setFrom(from);

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


    Response response;
    try {
      response = this.sendGrid.api(request);
      System.out.println("res: " + response.getBody());

      int statusCode = response.getStatusCode();

      if (statusCode >= 200 && statusCode <= 299) {
        // no error
        email.setError(null);
        log.info("Email sent res={}", response.getBody());

        log.info("`{}` sent to {}", subject, sendTo);

        return new DeliveryStatus(true, DeliveryStatus.DELIVERED);
      }



      throw new RuntimeException("email send status=failed");

    } catch (IOException e) {
      return new DeliveryStatus(false, e.getLocalizedMessage());
    }


  }

}
