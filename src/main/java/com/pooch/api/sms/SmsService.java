package com.pooch.api.sms;

public interface SmsService {

    boolean sendSMS(int countryCode, int phoneNumber, String message);
}
