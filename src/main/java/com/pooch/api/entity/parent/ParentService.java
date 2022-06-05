package com.pooch.api.entity.parent;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.ParentDTO;
import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.dto.S3FileDTO;

public interface ParentService {

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

    List<S3FileDTO> uploadProfileImages(String uuid, List<MultipartFile> images);

    void signOut(String token);
    
    Parent findByUuid(String uuid);

    ParentDTO updateProfile(ParentUpdateDTO parentUpdateDTO);

    ApiDefaultResponseDTO requestPhoneNumberVerification(String uuid,
        PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO);

    PhoneNumberVerificationDTO verifyNumberWithCode(String uuid,
        PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO);

    S3FileDTO uploadProfileImage(String uuid, MultipartFile image);
}
