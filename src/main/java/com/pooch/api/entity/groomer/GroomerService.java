package com.pooch.api.entity.groomer;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.dto.S3FileDTO;

public interface GroomerService {

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

    GroomerDTO updateProfile(GroomerUpdateDTO petSitterUpdateDTO);

    List<S3FileDTO> uploadProfileImages(String uuid, List<MultipartFile> images);

    List<S3FileDTO> uploadContractDocuments(String uuid, List<MultipartFile> images);
}
