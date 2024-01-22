package com.pooch.api.entity.parent;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.ParentCancellationRequestDTO;
import com.pooch.api.dto.ParentUpdateDTO;

public interface ParentValidatorService {

    Parent validateUploadProfileImages(String uuid, List<MultipartFile> images);

    Parent validateUpdateProfile(ParentUpdateDTO parentUpdateDTO);

    Parent validateUploadProfileImage(String uuid, MultipartFile image);

    Parent validateAccountCancellation(ParentCancellationRequestDTO cancellationRequest);
}
