package com.pooch.api.library.twilio.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.secretsmanager.TwilioSecrets;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsServiceImp implements SmsService {

    @Autowired
    private TwilioSecrets twilioSecrets;

    @Override
    public String sendSMS(int countryCode, long phoneNumber, String message) {

        String status = null;
        try {

            if (countryCode <= 0) {
                countryCode = 1;
            }

            Twilio.init(twilioSecrets.getAccountSid(), twilioSecrets.getAuthToken());

            // @formatter:off
            Message msg = Message.creator(
                    new com.twilio.type.PhoneNumber("+" + countryCode + phoneNumber), 
                    new com.twilio.type.PhoneNumber("+1" + twilioSecrets.getSmsSender()), 
                    message).create();
            // @formatter:on
            status = msg.getSid() + "-" + msg.getStatus();
            return status;

        } catch (Exception e) {
            log.warn("Twilio exception, msg={}, localMsg={}", e.getMessage(), e.getLocalizedMessage());
            throw new ApiException("Invalid phone number", e.getMessage());
        }
    }

}
