package com.pooch.api.entity.notification.email;

import java.util.Map;
import org.springframework.scheduling.annotation.Async;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.notification.Notification;
import com.pooch.api.entity.notification.email.template.EmailTemplate;
import com.pooch.api.entity.parent.Parent;

public interface EmailService {

  void send(Groomer groomer, EmailTemplate emailTemplate, Map<String, String> data);

  void send(Parent parent, EmailTemplate emailTemplate, Map<String, String> data);

  // @Async
  // void sendWelcome(User user);
  //
  // @Async
  // void sendPasswordResetRequest(User user, PasswordReset passwordReset);
}
