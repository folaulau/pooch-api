package com.pooch.api.entity.groomer;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import com.pooch.api.dto.BanListingDTO;
import com.pooch.api.dto.GroomerCreateListingDTO;
import com.pooch.api.dto.GroomerCreateProfileDTO;
import com.pooch.api.dto.GroomerSearchParamsDTO;
import com.pooch.api.dto.GroomerUpdateListingDTO;
import com.pooch.api.dto.SettingsUpdateDTO;

public interface GroomerValidatorService {

//    Groomer validateUpdateProfile(GroomerUpdateDTO petSitterUpdateDTO);

    Groomer validateUploadProfileImages(String uuid, List<MultipartFile> images);

    Groomer validateUploadContracts(String uuid, List<MultipartFile> images);

    void validateSearch(GroomerSearchParamsDTO filters);

    Groomer validateCreateUpdateProfile(GroomerCreateProfileDTO groomerCreateProfileDTO);

    Groomer validateCreateListing(GroomerCreateListingDTO groomerCreateListingDTO);

    Groomer validateSettingsUpdate(SettingsUpdateDTO settingsUpdateDTO);

    Groomer validateUpdateListing(GroomerUpdateListingDTO groomerUpdateListingDTO);

    Groomer validateBanListing(@Valid BanListingDTO banListingDTO);
}
