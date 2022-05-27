package com.pooch.api.entity.notification;

import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;

public interface NotificationService {

  void sendWelcomeNotificationToGroomer(Groomer groomer);

  void sendWelcomeNotificationToParent(Parent parent);
}
