package com.pooch.api.entity.phonenumber;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.sms.SmsService;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PhoneNumberServiceImp implements PhoneNumberService {

    @Autowired
    private PhoneNumberVerificationDAO  phoneNumberVerificationDAO;

    @Autowired
    private SmsService                  smsServive;

    @Autowired
    private PhoneNumberValidatorService phoneNumberValidatorService;

    @Autowired
    private EntityDTOMapper             entityDTOMapper;

    @Override
    public ApiDefaultResponseDTO requestVerification(PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO) {

        Integer code = RandomGeneratorUtils.getIntegerWithin(1000000, 9999999);

        PhoneNumberVerification phoneNumberVerification = new PhoneNumberVerification();
        phoneNumberVerification.setPhoneNumber(phoneNumberRequestVerificationDTO.getPhoneNumber() + "");
        phoneNumberVerification.setExpiredAt(LocalDateTime.now().plusMinutes(10));
        phoneNumberVerification.setVerificationCode(code + "");
        phoneNumberVerification.setCountryCode(phoneNumberRequestVerificationDTO.getCountryCode() + "");
        phoneNumberVerification.setPhoneVerified(false);

        smsServive.sendSMS(phoneNumberRequestVerificationDTO.getCountryCode(), phoneNumberRequestVerificationDTO.getPhoneNumber(), "Your PoochApp verification code is: " + code);

        phoneNumberVerification = phoneNumberVerificationDAO.save(phoneNumberVerification);

        return new ApiDefaultResponseDTO("Verification code has been sent to your phone");
    }

    @Override
    public PhoneNumberVerificationDTO verifyNumberWithCode(PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO) {
        PhoneNumberVerification phoneNumberVerification = phoneNumberValidatorService.validateVerificationNumberWithCode(phoneNumberVerificationDTO);
        phoneNumberVerification.setPhoneVerified(true);
        phoneNumberVerification = phoneNumberVerificationDAO.save(phoneNumberVerification);
        return entityDTOMapper.mapPhoneNumberVerificationToPhoneNumberVerificationDTO(phoneNumberVerification);
    }

}
