package com.pooch.api.entity.phonenumber;

import java.util.Optional;

public interface PhoneNumberVerificationDAO {

    PhoneNumberVerification save(PhoneNumberVerification phoneNumberVerification);

    Optional<PhoneNumberVerification> getByUuid(String uuid);

    Optional<PhoneNumberVerification> getByNumberAndCode(Long phoneNumber, String code);
}
