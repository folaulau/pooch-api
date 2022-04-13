package com.pooch.api.entity.groomer;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.pooch.api.dto.GroomerSearchParamsDTO;
import com.pooch.api.dto.GroomerUpdateDTO;

public interface GroomerValidatorService {

    Groomer validateUpdateProfile(GroomerUpdateDTO petSitterUpdateDTO);

    Groomer validateUploadProfileImages(String uuid, List<MultipartFile> images);

    Groomer validateUploadContracts(String uuid, List<MultipartFile> images);

    void validateSearch(GroomerSearchParamsDTO filters);
}
