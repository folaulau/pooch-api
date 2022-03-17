package com.pooch.api.entity.phonenumber;

import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;

public interface PhoneNumberValidatorService {

    PhoneNumberVerification validateVerificationNumberWithCode(PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO);

}
