package com.pooch.api.entity.notification;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.core.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.demo.Demo;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.subscriber.Subscriber;
import com.pooch.api.entity.notification.email.Email;
import com.pooch.api.entity.notification.email.EmailService;
import com.pooch.api.entity.notification.email.EmailStatus;
import com.pooch.api.entity.notification.email.dynamicdata.DemoInfo;
import com.pooch.api.entity.notification.email.dynamicdata.GroomerDemoRequest;
import com.pooch.api.entity.notification.email.template.EmailTemplate;
import com.pooch.api.entity.notification.email.template.EmailTemplateUuid;
import com.pooch.api.entity.notification.pushnotification.PushNotificationService;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationServiceImp implements NotificationService {

    @Autowired
    private EmailService            emailService;

    @Autowired
    private NotificationRepository  notificationRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Async
    @Override
    public void sendWelcomeNotificationToGroomer(Groomer groomer) {
        log.info("sendWelcomeNotificationToGroomer");

        Optional<Notification> optNtc = null;

        try {

            optNtc = notificationRepository.findByUuid(NotificationUuid.WELCOME_GROOMER);

        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
        }

        Notification ntc = null;

        if (optNtc != null && optNtc.isPresent()) {
            ntc = optNtc.get();

            EmailTemplate emailTemplate = new ArrayList<>(ntc.getEmailTemplates()).get(0);
            emailTemplate.setSenderTemplateId("d-8524f786e06c42159f2c850047af6c4a");
            emailTemplate.setSendToUser(UserType.groomer);
            ntc.setEmailTemplates(Set.of(emailTemplate));
        } else {
            log.info("template {} not found", NotificationUuid.WELCOME_GROOMER.toString());
            ntc = new Notification();
            EmailTemplate emailTemplate = new EmailTemplate();
            emailTemplate.setSenderTemplateId("d-8524f786e06c42159f2c850047af6c4a");
            emailTemplate.setSendToUser(UserType.groomer);
            ntc.setEmailTemplates(Set.of(emailTemplate));
        }

        log.info("ntc={}", ntc.toJson());

        java.util.Map<String, Object> data = Map.of("Name", groomer.getFullName());

        log.info("ntc={}", ntc.toJson());

        sendEmails(ntc, data, groomer, null);

        if (ntc.getPushNotification() != null && ntc.getPushNotification() == true) {
            pushNotificationService.push(ntc, data);
        }

    }

    @Async
    @Override
    public void sendWelcomeNotificationToParent(Parent parent) {
        Notification ntc = notificationRepository.findByUuid(NotificationUuid.WELCOME_PARENT).get();

        log.info("ntc={}", ntc.toJson());

        java.util.Map<String, Object> data = Map.of("name", Optional.ofNullable(parent.getFullName()).orElse(""));

        sendEmails(ntc, data, null, parent);

        if (ntc.getPushNotification() != null && ntc.getPushNotification() == true) {
            pushNotificationService.push(ntc, data);
        }
    }

    @Async
    @Override
    public void sendBookingDetailsUponBooking(Booking booking, Parent parent, Groomer groomer) {
        Notification ntc = notificationRepository.findByUuid(NotificationUuid.SEND_NEW_BOOKING_DETAILS).get();

        log.info("ntc={}", ntc.toJson());

        java.util.Map<String, Object> data = Map.of("details", booking.toJson());

        sendEmails(ntc, data, groomer, parent);

        if (ntc.getPushNotification() != null && ntc.getPushNotification() == true) {
            pushNotificationService.push(ntc, data);
        }
    }

    private void sendEmails(Notification ntc, java.util.Map<String, Object> data, Groomer groomer, Parent parent) {
        if (ntc.getEmail() != null && ntc.getEmail() == true) {

            ntc.getEmailTemplates().stream().forEach(template -> {
                if (template.getSendToUser() != null) {
                    if (template.getSendToUser().equals(UserType.groomer) && groomer != null) {
                        emailService.send(groomer, template, data);
                    } else if (template.getSendToUser().equals(UserType.parent) && parent != null) {
                        emailService.send(parent, template, data);
                    }
                }

            });
        }
    }

    @Override
    public void sendBookingCancellation(Booking booking, Parent parent, Groomer groomer) {
        // TODO Auto-generated method stub

    }

    @Async
    @Override
    public void sendDemoConfirmationToGroomer(Demo demo) {
        Notification ntc = new Notification();
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setSenderTemplateId("d-f87b14bed1754db4baecdde2aa037b4e");
        emailTemplate.setSendToUser(UserType.groomer);
        emailTemplate.setSubject("Demo Request Confirmation");
        emailTemplate.setUuid(EmailTemplateUuid.DEMO_CONFIRMATION_EMAIL);
        ntc.setEmailTemplates(Set.of(emailTemplate));

        log.info("ntc={}", ntc.toJson());

        GroomerDemoRequest groomerDemoRequest = GroomerDemoRequest.builder().groomerFirstName(demo.getFirstName()).build();

        java.util.Map<String, Object> data = ObjectUtils.toMap(groomerDemoRequest);

        log.info("ntc={}", ntc.toJson());

        emailService.send(demo, emailTemplate, data);
    }

    @Override
    public void sendDemoRequestDataToAdmins(DemoInfo demoInfo) {
        Notification ntc = new Notification();
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setSenderTemplateId("d-9a6fab9ce06b47e3a41fe5ebe370e03e");
        emailTemplate.setSendToUser(UserType.groomer);
        emailTemplate.setSubject("Demo Request Information");
        emailTemplate.setUuid(EmailTemplateUuid.DEMO_INFO_EMAIL);
        ntc.setEmailTemplates(Set.of(emailTemplate));

        Email email = new Email();
        email.setSendTo("info@poochtech.com");
        email.addCC("eli@poochtech.com");
        email.addCC("yis@poochtech.com");
        email.addCC("folau@poochtech.com");
        email.setStatus(EmailStatus.SENDING);
        email.setSubject(emailTemplate.getSubject());

        email.setTemplateUuid(emailTemplate.getUuid());
        email.setEmailTemplateId(emailTemplate.getSenderTemplateId());

        java.util.Map<String, Object> data = ObjectUtils.toMap(demoInfo);

        email.setDynamicData(data);

        log.info("email={}", email.toJson());

        emailService.send(email, emailTemplate, data);
    }

    @Override
    public void sendGroomerSubcribingNtc(Subscriber subscriber) {
        Notification ntc = new Notification();
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setSenderTemplateId("d-aa877d8223c24aa4b68b8e227dc7858e");
        emailTemplate.setSendToUser(UserType.groomer);
        emailTemplate.setSubject("Groomer Subscribing");
        emailTemplate.setUuid(EmailTemplateUuid.GROOMER_SUBSCRIBER);
        ntc.setEmailTemplates(Set.of(emailTemplate));

        log.info("ntc={}", ntc.toJson());

        java.util.Map<String, Object> data = null;

        emailService.send(subscriber, emailTemplate, data);
    }

}
