package com.pooch.api.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.aws.secretsmanager.TwilioSecrets;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsServiceImp implements SmsService {

    @Autowired
    private TwilioSecrets twilioSecrets;

    @Override
    public boolean sendSMS(long phoneNumber, String message) {
        try {
            Twilio.init(twilioSecrets.getAccountSid(), twilioSecrets.getAuthToken());
            Message message = Message.creator(new com.twilio.type.PhoneNumber("+1" + phoneNumber), new com.twilio.type.PhoneNumber("+1" + twilioSecrets.getSmsSender()), message).create();
            return true;
        } catch (Exception e) {
            log.warn("Twilio exception, msg={}", e.getLocalizedMessage());
        }
        return false;
    }

}
