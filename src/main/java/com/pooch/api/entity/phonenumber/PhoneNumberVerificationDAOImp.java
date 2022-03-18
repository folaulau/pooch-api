package com.pooch.api.entity.phonenumber;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class PhoneNumberVerificationDAOImp implements PhoneNumberVerificationDAO {

    @Autowired
    private PhoneNumberVerificationRepository phoneNumberVerificationRepository;

    @Override
    public PhoneNumberVerification save(PhoneNumberVerification phoneNumberVerification) {
        return phoneNumberVerificationRepository.saveAndFlush(phoneNumberVerification);
    }

    @Override
    public Optional<PhoneNumberVerification> getByUuid(String uuid) {
        return phoneNumberVerificationRepository.findByUuid(uuid);
    }

    @Override
    public Optional<PhoneNumberVerification> getByNumberAndCountryCodeAndCode(int countryCode, long phoneNumber, String code) {
        return phoneNumberVerificationRepository.findByVerificationCodeAndPhoneNumberAndCountryCode(code.trim(), phoneNumber, countryCode);
    }
}
