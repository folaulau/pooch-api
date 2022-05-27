package com.pooch.api.entity.notification.pushnotification;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import com.pooch.api.entity.notification.email.Email;
import com.pooch.api.entity.notification.email.EmailStatus;
import com.pooch.api.entity.notification.email.template.EmailTemplateUuid;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum PushNotificationTemplateUuid {

  WELCOME_GROOMER_NTC;
}
