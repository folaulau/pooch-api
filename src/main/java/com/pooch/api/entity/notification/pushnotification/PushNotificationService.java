package com.pooch.api.entity.notification.pushnotification;

import java.util.Map;
import java.util.Set;
import com.pooch.api.entity.notification.Notification;
import com.pooch.api.entity.notification.pushnotification.token.PushNotificationToken;

public interface PushNotificationService {

  PushNotification send(PushNotification pushNotification, Set<PushNotificationToken> tokens);

  void push(Notification ntc, Map<String, Object> data);
}
