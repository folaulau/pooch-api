package com.pooch.api.sms;

public interface SmsService {

    boolean sendSMS(int countryCode, long phoneNumber, String message);
}
