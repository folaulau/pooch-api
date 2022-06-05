package com.pooch.api.entity.parent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.entity.pooch.PoochValidatorService;
import com.pooch.api.exception.ApiException;
import com.pooch.api.exception.ApiSubError;
import com.pooch.api.utils.FileValidatorUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParentValidatorServiceImp implements ParentValidatorService {

  @Autowired
  private ParentDAO parentDAO;

  @Autowired
  private PoochValidatorService poochValidatorService;

  private static Pattern phonePattern = Pattern.compile("^\\d{10}$");

  @Override
  public Parent validateUploadProfileImages(String uuid, List<MultipartFile> images) {

    Parent parent = parentDAO.getByUuid(uuid).orElseThrow(
        () -> new ApiException("Unable to upload image", "Parent not found for uuid=" + uuid));

    FileValidatorUtils.validateUploadImages(images);

    return parent;
  }

  @Override
  public Parent validateUpdateProfile(ParentUpdateDTO parentUpdateDTO) {

    String uuid = parentUpdateDTO.getUuid();

    Parent parent = parentDAO.getByUuid(uuid).orElseThrow(
        () -> new ApiException("Unable to update profile", "Parent not found for uuid=" + uuid));

    Long phoneNumber = parentUpdateDTO.getPhoneNumber();

    if (phoneNumber != null && !phonePattern.matcher("" + phoneNumber).matches()) {
      throw new ApiException("Invalid Phone Number", "Phone number must a valid 10 digit number");
    }

    Set<PoochCreateUpdateDTO> poochCreateUpdateDTOs = parentUpdateDTO.getPooches();

    if (poochCreateUpdateDTOs != null && poochCreateUpdateDTOs.size() > 0) {
      for (PoochCreateUpdateDTO pooch : poochCreateUpdateDTOs) {

        poochValidatorService.validateCreateUpdatePooch(parent, pooch);

      }
    }

    return parent;
  }

  @Override
  public Parent validateUploadProfileImage(String uuid, MultipartFile image) {
    Parent parent = parentDAO.getByUuid(uuid).orElseThrow(
        () -> new ApiException("Unable to upload image", "Parent not found for uuid=" + uuid));

    FileValidatorUtils.validateUploadImages(Arrays.asList(image));

    return parent;
  }

}
