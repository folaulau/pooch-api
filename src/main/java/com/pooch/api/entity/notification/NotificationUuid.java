package com.pooch.api.entity.notification;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
public enum NotificationUuid {

  WELCOME_GROOMER,

  WELCOME_PARENT,

  WELCOME_POOCH_EMPLOYEE,

  /**
   * send notification to groomer and parent of new booking
   */
  SEND_NEW_BOOKING_DETAILS;

}
