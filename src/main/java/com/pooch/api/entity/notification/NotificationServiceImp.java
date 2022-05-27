package com.pooch.api.entity.notification;

import org.springframework.beans.factory.annotation.Autowired;
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
  private PushNotificationService pushNotificationService;

  @Override
  public void sendWelcomeNotificationToGroomer(Groomer groomer) {
    // TODO Auto-generated method stub

  }

  @Override
  public void sendWelcomeNotificationToParent(Parent parent) {
    // TODO Auto-generated method stub

  }

}
