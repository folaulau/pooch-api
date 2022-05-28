package com.pooch.api.entity.notification;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Optional<Notification> findByUuid(NotificationUuid uuid);
}
