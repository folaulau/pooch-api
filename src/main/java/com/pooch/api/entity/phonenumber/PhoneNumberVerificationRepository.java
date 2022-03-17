package com.pooch.api.entity.phonenumber;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneNumberVerificationRepository extends JpaRepository<PhoneNumberVerification, Long> {

    Optional<PhoneNumberVerification> findByVerificationCode(String code);

    Optional<PhoneNumberVerification> findByUuid(String uuid);

    Optional<PhoneNumberVerification> findByVerificationCodeAndPhoneNumber(String code, String phoneNumber);

}
