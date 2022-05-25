package com.pooch.api.entity.phonenumber;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.entity.parent.Parent;

public interface PhoneNumberService {

  ApiDefaultResponseDTO requestVerification(Parent parent,
      PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO);

  PhoneNumberVerification verifyNumberWithCode(Parent parent,
      PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO);

}
