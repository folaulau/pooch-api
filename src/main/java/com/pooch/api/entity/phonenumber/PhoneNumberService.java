package com.pooch.api.entity.phonenumber;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;

public interface PhoneNumberService {

    ApiDefaultResponseDTO requestVerification(PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO);

    PhoneNumberVerification verifyNumberWithCode(PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO);

}
