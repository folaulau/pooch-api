package com.pooch.api.entity.parent;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.ParentDTO;
import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.S3FileDTO;

public interface ParentService {

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

    List<S3FileDTO> uploadProfileImages(String uuid, List<MultipartFile> images);

    void signOut(String token);

    ParentDTO updateProfile(ParentUpdateDTO parentUpdateDTO);
}
