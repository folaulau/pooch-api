package com.pooch.api.sms;

public interface SmsService {

    String sendSMS(int countryCode, long phoneNumber, String message);
}
