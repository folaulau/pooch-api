package com.pooch.api.entity.notification;

import org.springframework.scheduling.annotation.Async;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;

public interface NotificationService {

  @Async
  void sendWelcomeNotificationToGroomer(Groomer groomer);

  @Async
  void sendWelcomeNotificationToParent(Parent parent);

  @Async
  void sendBookingDetailsUponBooking(Booking booking, Parent parent, Groomer groomer);
}
