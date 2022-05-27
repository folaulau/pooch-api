package com.pooch.api.entity.notification.pushnotification.token;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PushNotificationTokenRepository
    extends JpaRepository<PushNotificationToken, Long> {
}
