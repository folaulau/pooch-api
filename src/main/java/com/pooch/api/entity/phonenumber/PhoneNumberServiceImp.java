package com.pooch.api.entity.phonenumber;

import java.time.LocalDateTime;

import com.pooch.api.entity.parent.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.library.twilio.sms.SmsService;
import com.pooch.api.utils.ObjectUtils;
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

    @Value("${phonenumber.verification.code.timer}")
    private int                         verificationCodeTimer;

    @Override
    public ApiDefaultResponseDTO requestVerification(Parent parent, PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO) {

        Integer code = RandomGeneratorUtils.getIntegerWithin(1000000, 9999999);

        PhoneNumberVerification phoneNumberVerification = new PhoneNumberVerification();
        phoneNumberVerification.setPhoneNumber(phoneNumberRequestVerificationDTO.getPhoneNumber());
        phoneNumberVerification.setExpiredAt(LocalDateTime.now().plusMinutes(verificationCodeTimer));
        phoneNumberVerification.setVerificationCode(code + "");
        phoneNumberVerification.setCountryCode(phoneNumberRequestVerificationDTO.getCountryCode());
        phoneNumberVerification.setPhoneVerified(false);
        phoneNumberVerification.setParent(parent);

        String sentStatus = smsServive.sendSMS(phoneNumberRequestVerificationDTO.getCountryCode(), phoneNumberRequestVerificationDTO.getPhoneNumber(), "Your PoochApp verification code is: " + code);

        phoneNumberVerification.setSentStatus(sentStatus);
        phoneNumberVerification = phoneNumberVerificationDAO.save(phoneNumberVerification);

        log.info("phoneNumberVerification={}", ObjectUtils.toJson(phoneNumberVerification));

        return new ApiDefaultResponseDTO("Verification code has been sent to your phone");
    }

    @Override
    public PhoneNumberVerification verifyNumberWithCode(Parent parent, PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO) {
        PhoneNumberVerification phoneNumberVerification = phoneNumberValidatorService.validateVerificationNumberWithCode(phoneNumberVerificationDTO);
        phoneNumberVerification.setPhoneVerified(true);
        phoneNumberVerification = phoneNumberVerificationDAO.save(phoneNumberVerification);
        return phoneNumberVerification;
    }

}
