package com.pooch.api.entity.groomer;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.GroomerCreateListingDTO;
import com.pooch.api.dto.GroomerCreateProfileDTO;
import com.pooch.api.dto.GroomerSearchParamsDTO;
import com.pooch.api.dto.SettingsUpdateDTO;

public interface GroomerValidatorService {

//    Groomer validateUpdateProfile(GroomerUpdateDTO petSitterUpdateDTO);

    Groomer validateUploadProfileImages(String uuid, List<MultipartFile> images);

    Groomer validateUploadContracts(String uuid, List<MultipartFile> images);

    void validateSearch(GroomerSearchParamsDTO filters);

    Groomer validateCreateUpdateProfile(GroomerCreateProfileDTO groomerCreateProfileDTO);

    Groomer validateCreateUpdateListing(GroomerCreateListingDTO groomerCreateListingDTO);

    Groomer validateSettingsUpdate(SettingsUpdateDTO settingsUpdateDTO);
}
