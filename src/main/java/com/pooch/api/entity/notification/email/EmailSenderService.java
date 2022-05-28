package com.pooch.api.entity.notification.email;

import com.pooch.api.entity.UserType;
import com.sun.mail.smtp.SMTPSendFailedException;

public interface EmailSenderService {

  DeliveryStatus sendEmail(Email email) throws SMTPSendFailedException;


  DeliveryStatus sendEmail(Email email, UserType userType) throws SMTPSendFailedException;

  // DeliveryStatus reportBadEmail(SMTPSendFailedException ex, Email email);

}
