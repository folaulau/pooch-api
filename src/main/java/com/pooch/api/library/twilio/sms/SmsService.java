package com.pooch.api.library.twilio.sms;

public interface SmsService {

    String sendSMS(int countryCode, long phoneNumber, String message);
}
