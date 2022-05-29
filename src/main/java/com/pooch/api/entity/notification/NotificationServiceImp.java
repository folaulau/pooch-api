package com.pooch.api.entity.notification;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

    if (ntc.getEmail() != null && ntc.getEmail() == true) {
      emailService.send(groomer, ntc, data);
    }

    if (ntc.getPushNotification() != null && ntc.getPushNotification() == true) {
      pushNotificationService.push(ntc, data);
    }

  }

  @Async
  @Override
  public void sendWelcomeNotificationToParent(Parent parent) {
    Notification ntc = notificationRepository.findByUuid(NotificationUuid.WELCOME_PARENT).get();

    log.info("ntc={}", ntc.toJson());

    java.util.Map<String, String> data = Map.of("name", parent.getFullName());

    if (ntc.getEmail() != null && ntc.getEmail() == true) {
      emailService.send(parent, ntc, data);
    }

    if (ntc.getPushNotification() != null && ntc.getPushNotification() == true) {
      pushNotificationService.push(ntc, data);
    }
  }

}
