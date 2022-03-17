package com.pooch.api.sms;

public interface SmsService {

    boolean sendSMS(long phoneNumber, String message);
}
