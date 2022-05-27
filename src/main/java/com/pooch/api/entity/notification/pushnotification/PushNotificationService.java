package com.pooch.api.entity.notification.pushnotification;

import java.util.Set;
import com.pooch.api.entity.notification.pushnotification.token.PushNotificationToken;

public interface PushNotificationService {

  PushNotification send(PushNotification pushNotification, Set<PushNotificationToken> tokens);
}
