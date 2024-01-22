package com.pooch.api.entity.notification.email;

import java.util.Map;
import org.springframework.scheduling.annotation.Async;

import com.pooch.api.entity.demo.Demo;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.subscriber.Subscriber;
import com.pooch.api.entity.notification.Notification;
import com.pooch.api.entity.notification.email.template.EmailTemplate;
import com.pooch.api.entity.parent.Parent;

public interface EmailService {

    void send(Groomer groomer, EmailTemplate emailTemplate, Map<String, Object> data);

    void send(Parent parent, EmailTemplate emailTemplate, Map<String, Object> data);

    void send(Demo demo, EmailTemplate emailTemplate, Map<String, Object> data);

    void send(Subscriber subscriber, EmailTemplate emailTemplate, Map<String, Object> data);
    
    void send(Email email, EmailTemplate emailTemplate, Map<String, Object> data);

    // @Async
    // void sendWelcome(User user);
    //
    // @Async
    // void sendPasswordResetRequest(User user, PasswordReset passwordReset);
}
