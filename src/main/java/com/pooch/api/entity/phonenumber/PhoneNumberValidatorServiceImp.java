package com.pooch.api.entity.phonenumber;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PhoneNumberValidatorServiceImp implements PhoneNumberValidatorService {

    @Autowired
    private PhoneNumberVerificationDAO phoneNumberVerificationDAO;

    @Override
    public PhoneNumberVerification validateVerificationNumberWithCode(PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO) {
        Integer countryCode = phoneNumberVerificationDTO.getCountryCode();
        if (null == countryCode || countryCode <= 0) {
            throw new ApiException("Invalid country code");
        }

        Long phoneNumber = phoneNumberVerificationDTO.getPhoneNumber();
        if (null == phoneNumber || countryCode <= 0) {
            throw new ApiException("Invalid phone number", "phoneNumber=" + phoneNumber);
        }

        String code = phoneNumberVerificationDTO.getCode();

        if (null == code || code.trim().isEmpty()) {
            throw new ApiException("Invalid code", "code=" + code);
        }

        Optional<PhoneNumberVerification> optNumberVerification = phoneNumberVerificationDAO.getByNumberAndCode(phoneNumber, code);

        if (!optNumberVerification.isPresent()) {
            throw new ApiException("Invalid code", "verification not found for code=" + code + " and phoneNumber=" + phoneNumber);
        }

        PhoneNumberVerification phoneNumberVerification = optNumberVerification.get();

        LocalDateTime expiredAt = phoneNumberVerification.getExpiredAt();

        if (expiredAt.isAfter(LocalDateTime.now())) {
            throw new ApiException("Expired code", "code expires in 10 mins from when it's sent");
        }

        return phoneNumberVerification;
    }

}
