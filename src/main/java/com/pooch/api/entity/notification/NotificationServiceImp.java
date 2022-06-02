package com.pooch.api.entity.notification;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.pooch.api.entity.UserType;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.notification.email.EmailService;
import com.pooch.api.entity.notification.pushnotification.PushNotificationService;
import com.pooch.api.entity.parent.Parent;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class NotificationServiceImp implements NotificationService {

  @Autowired
  private EmailService emailService;

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private PushNotificationService pushNotificationService;

  @Async
  @Override
  public void sendWelcomeNotificationToGroomer(Groomer groomer) {

    Notification ntc = notificationRepository.findByUuid(NotificationUuid.WELCOME_GROOMER).get();

    log.info("ntc={}", ntc.toJson());

    java.util.Map<String, String> data = Map.of("name", groomer.getFullName());

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

    java.util.Map<String, String> data = Map.of("name", Optional.ofNullable(parent.getFullName()).orElse(""));

    sendEmails(ntc, data, null, parent);

    if (ntc.getPushNotification() != null && ntc.getPushNotification() == true) {
      pushNotificationService.push(ntc, data);
    }
  }

  @Async
  @Override
  public void sendBookingDetailsUponBooking(Booking booking, Parent parent, Groomer groomer) {
    Notification ntc =
        notificationRepository.findByUuid(NotificationUuid.SEND_NEW_BOOKING_DETAILS).get();

    log.info("ntc={}", ntc.toJson());

    java.util.Map<String, String> data = Map.of("details", booking.toJson());

    sendEmails(ntc, data, groomer, parent);

    if (ntc.getPushNotification() != null && ntc.getPushNotification() == true) {
      pushNotificationService.push(ntc, data);
    }
  }

  private void sendEmails(Notification ntc, java.util.Map<String, String> data, Groomer groomer,
      Parent parent) {
    if (ntc.getEmail() != null && ntc.getEmail() == true) {

      ntc.getEmailTemplates().stream().forEach(template -> {
        if (template.getSendToUser() != null) {
          if (template.getSendToUser().equals(UserType.Groomer) && groomer != null) {
            emailService.send(groomer, template, data);
          } else if (template.getSendToUser().equals(UserType.Parent) && parent != null) {
            emailService.send(parent, template, data);
          }
        }

      });
    }
  }

}
