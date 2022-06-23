package com.pooch.api.entity.groomer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.AddressCreateUpdateDTO;
import com.pooch.api.dto.CareServiceUpdateDTO;
import com.pooch.api.dto.CustomSort;
import com.pooch.api.dto.GroomerCreateListingDTO;
import com.pooch.api.dto.GroomerCreateProfileDTO;
import com.pooch.api.dto.GroomerSearchParamsDTO;
import com.pooch.api.dto.GroomerUpdateListingDTO;
import com.pooch.api.dto.SettingsUpdateDTO;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.address.AddressDAO;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.groomer.careservice.type.GroomerServiceTypeService;
import com.pooch.api.entity.groomer.careservice.type.GroomerServiceTypeServiceImp;
import com.pooch.api.entity.pooch.PoochSize;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.exception.ApiSubError;
import com.pooch.api.utils.FileValidatorUtils;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerValidatorServiceImp implements GroomerValidatorService {

  @Autowired
  private GroomerDAO groomerDAO;

  @Autowired
  private CareServiceDAO careServiceDAO;

  @Autowired
  private AddressDAO addressDAO;

  private static Pattern phonePattern = Pattern.compile("^\\d{10}$");

  @Autowired
  private GroomerServiceTypeService groomerServiceTypeService;

  @Override
  public Groomer validateCreateUpdateProfile(GroomerCreateProfileDTO groomerCreateProfileDTO) {

    log.info("validateCreateUpdateProfile={}", ObjectUtils.toJson(groomerCreateProfileDTO));

    String uuid = groomerCreateProfileDTO.getUuid();

    if (uuid == null || uuid.isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "uuid is empty. uuid=" + uuid);
    }

    Optional<Groomer> optGroomer = groomerDAO.getByUuid(uuid);

    if (!optGroomer.isPresent()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "Groomer not found for uuid=" + uuid);
    }

    Groomer groomer = optGroomer.get();

    ApiError error = new ApiError();

    String firstName = groomerCreateProfileDTO.getFirstName();

    if (firstName == null || firstName.trim().isEmpty()) {
      error.addError(new ApiSubError("Invalid First Name", "groomer", "firstName", ""));
    }

    String lastName = groomerCreateProfileDTO.getLastName();

    if (lastName == null || lastName.trim().isEmpty()) {
      error.addError(new ApiSubError("Invalid Last Name", "groomer", "lastName", ""));
    }

    String businessName = groomerCreateProfileDTO.getBusinessName();
    if (businessName == null || businessName.trim().isEmpty()) {
      error.addError(new ApiSubError("Invalid BusinessName", "groomer", "businessName", ""));
    }

    Long phoneNumber = groomerCreateProfileDTO.getPhoneNumber();

    if (phoneNumber == null || !phonePattern.matcher("" + phoneNumber).matches()) {
      error.addError(new ApiSubError("Invalid Phone Number", "groomer", "phoneNumber", ""));
    }

    AddressCreateUpdateDTO address = groomerCreateProfileDTO.getAddress();

    if (address == null || address.isValidAddress() == false) {
      error.addError(new ApiSubError("Invalid Address", "address", "address", ""));
    }

    Set<CareServiceUpdateDTO> careServices = groomerCreateProfileDTO.getCareServices();

    if (careServices == null || careServices.size() <= 0) {
      error.addSubError("careServices is required");
    }

    if (error.hasErrors()) {
      error.setMessage(ApiError.DEFAULT_MSG);
      error.setStatus(HttpStatus.BAD_REQUEST);
      throw new ApiException(error);
    }

    return groomer;
  }

  @Override
  public Groomer validateCreateListing(GroomerCreateListingDTO groomerCreateListingDTO) {
    log.info("validateCreateUpdateListing={}", ObjectUtils.toJson(groomerCreateListingDTO));

    String uuid = groomerCreateListingDTO.getUuid();

    if (uuid == null || uuid.isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "uuid is empty. uuid=" + uuid);
    }

    Optional<Groomer> optGroomer = groomerDAO.getByUuid(uuid);

    if (!optGroomer.isPresent()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "Groomer not found for uuid=" + uuid);
    }

    Groomer groomer = optGroomer.get();

    ApiError error = new ApiError();

    Set<CareServiceUpdateDTO> careServices = groomerCreateListingDTO.getCareServices();

    if (careServices == null || careServices.size() <= 0) {
      error.addSubError("careServices is required");
    }

    for (CareServiceUpdateDTO careService : careServices) {
      if (careService == null) {
        error.addSubError("careService is empty");
        break;
      }

      String name = careService.getName();

      if (name == null || name.isEmpty()) {
        error.addSubError("service name is emtpy");
      }

      /**
       * as of 2022-06-08, make sure all prices are filled
       */

      // if (careService.isServiceSmall() && careService.getSmallPrice() == null) {
      // error.addSubError("serviceSmall is turned on but smallPrice is not specified");
      // } else if (!careService.isServiceSmall()) {
      // careService.setSmallPrice(null);
      // }

      if (careService.getSmallPrice() == null
          || (careService.getSmallPrice() < 0 || careService.getSmallPrice() > 10000)) {
        error.addSubError("smallPrice is iinvalid");
      }

      // if (careService.isServiceMedium() && careService.getMediumPrice() == null) {
      // error.addSubError("serviceMedium is turned on but mediumPrice is not specified");
      // } else if (!careService.isServiceMedium()) {
      // careService.setMediumPrice(null);
      // }

      if (careService.getMediumPrice() == null
          || (careService.getMediumPrice() < 0 || careService.getMediumPrice() > 10000)) {
        error.addSubError("mediumPrice is iinvalid");
      }

      // if (careService.isServiceLarge() && careService.getLargePrice() == null) {
      // error.addSubError("serviceLarge is turned on but largePrice is not specified");
      // } else if (!careService.isServiceLarge()) {
      // careService.setLargePrice(null);
      // }

      if (careService.getLargePrice() == null
          || (careService.getLargePrice() < 0 || careService.getLargePrice() > 10000)) {
        error.addSubError("largePrice is iinvalid");
      }
    }

    if (error.hasErrors()) {
      error.setMessage(ApiError.DEFAULT_MSG);
      error.setStatus(HttpStatus.BAD_REQUEST);
      throw new ApiException(error);
    }

    return groomer;
  }

  // @Override
  // public Groomer validateUpdateProfile(GroomerUpdateDTO groomerUpdateDTO) {
  //
  // log.info("groomerUpdateDTO={}", ObjectUtils.toJson(groomerUpdateDTO));
  //
  // String uuid = groomerUpdateDTO.getUuid();
  //
  // if (uuid == null || uuid.isEmpty()) {
  // throw new ApiException(ApiError.DEFAULT_MSG, "uuid is empty. uuid=" + uuid);
  // }
  //
  // Optional<Groomer> optGroomer = groomerDAO.getByUuid(uuid);
  //
  // if (!optGroomer.isPresent()) {
  // throw new ApiException(ApiError.DEFAULT_MSG, "Groomer not found for uuid=" + uuid);
  // }
  //
  // Groomer groomer = optGroomer.get();
  // //
  // // Optional.ofNullable(groomerUpdateDTO.getEmail()).ifPresent(email -> {
  // // if (!email.trim().equalsIgnoreCase(groomer.getEmail())) {
  // // if (groomerDAO.existEmail(email)) {
  // // log.debug("email is taken");
  // // throw new ApiException("Email is taken");
  // // }
  // // }
  // // });
  //
  // if (groomer.getStatus().equals(GroomerStatus.SIGNING_UP)) {
  // GroomerSignUpStatus signUpStatus = Optional.ofNullable(groomerUpdateDTO.getSignUpStatus())
  // .orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "signUpStatus is required", "status="
  // + Arrays.asList(GroomerSignUpStatus.values())));
  //
  // }
  //
  // Set<CareServiceUpdateDTO> careServicesDTOs = groomerUpdateDTO.getCareServices();
  //
  // if (careServicesDTOs != null) {
  // Set<String> serviceNames = new HashSet<>();
  //
  // for (CareServiceUpdateDTO careServiceUpdateDTO : careServicesDTOs) {
  //
  // final String serviceName = careServiceUpdateDTO.getName();
  //
  // /**
  // * validate service name
  // */
  // if (serviceName == null || serviceName.isBlank()) {
  // throw new ApiException("Service name is required", "serviceName=" + serviceName);
  // }
  //
  // if (groomerServiceTypeService.getByName(serviceName) == null) {
  // throw new ApiException("Invalid service name", "service name=" + serviceName, "valid
  // serviceNames=" + GroomerServiceTypeServiceImp.dict.values(),
  // "care service name is case sensitive");
  // }
  //
  // if (serviceNames.contains(serviceName)) {
  // throw new ApiException(serviceName + " is a duplicate", "serviceName=" + serviceName);
  // }
  //
  // /**
  // * validate prices
  // */
  // if (careServiceUpdateDTO.isServiceSmall()) {
  // Double smallPrice = careServiceUpdateDTO.getSmallPrice();
  // /**
  // * assume 10 million must be a mistake
  // */
  // if (smallPrice == null || smallPrice <= -1 || smallPrice >= 10000000) {
  // throw new ApiException(serviceName + ", smallPrice is valid", "smallPrice=" + smallPrice);
  // }
  // }
  //
  // if (careServiceUpdateDTO.isServiceMedium()) {
  // Double mediumPrice = careServiceUpdateDTO.getSmallPrice();
  // /**
  // * assume 10 million must be a mistake
  // */
  // if (mediumPrice == null || mediumPrice <= -1 || mediumPrice >= 10000000) {
  // throw new ApiException(serviceName + ", mediumPrice is valid", "mediumPrice=" + mediumPrice);
  // }
  // }
  //
  // if (careServiceUpdateDTO.isServiceLarge()) {
  // Double largePrice = careServiceUpdateDTO.getSmallPrice();
  // /**
  // * assume 10 million must be a mistake
  // */
  // if (largePrice == null || largePrice <= -1 || largePrice >= 10000000) {
  // throw new ApiException(serviceName + ", largePrice is valid", "largePrice=" + largePrice);
  // }
  // }
  //
  // /**
  // * validate updating existing care service
  // */
  // Optional.ofNullable(careServiceUpdateDTO.getUuid()).ifPresent(careServiceUuid -> {
  // if (!careServiceDAO.existByUuidAndGroomer(careServiceUuid, groomer.getId())) {
  // throw new ApiException(ApiError.DEFAULT_MSG, "uuid not found for this care service", "make sure
  // you pass the correct uuid for careService");
  // }
  // });
  //
  // serviceNames.add(serviceName);
  // }
  // }
  //
  // AddressCreateUpdateDTO addressDTO = groomerUpdateDTO.getAddress();
  //
  // if (addressDTO != null) {
  // Double latitude = addressDTO.getLatitude();
  //
  // if (latitude == null) {
  // log.info("Latitude is required");
  // throw new ApiException("Address is invalid", "Latitude is required");
  // }
  // Double longitude = addressDTO.getLongitude();
  //
  // if (longitude == null) {
  // log.info("Longitude is required");
  // throw new ApiException("Address is invalid", "Longitude is required");
  // }
  // String addressUuid = addressDTO.getUuid();
  // if (addressUuid != null && !addressUuid.isEmpty()) {
  //
  // Address address = groomer.getAddress();
  //
  // if (address == null) {
  // throw new ApiException("Address not found", "Groomer does not have an existing address",
  // "address uuid does not belong to groomer");
  // }
  //
  // if (!address.getUuid().equalsIgnoreCase(addressUuid)) {
  // throw new ApiException("Address not found", "uuid=" + addressDTO.getUuid() + " not found",
  // "address may not belong to this user");
  // }
  // }
  // }
  //
  // return groomer;
  // }

  @Override
  public Groomer validateUploadProfileImages(String uuid, List<MultipartFile> images) {

    Optional<Groomer> optParent = groomerDAO.getByUuid(uuid);

    if (!optParent.isPresent()) {
      throw new ApiException("Unable to upload image(s)", "Groomer not found for uuid=" + uuid);
    }

    FileValidatorUtils.validateUploadImages(images);

    return optParent.get();
  }

  @Override
  public Groomer validateUploadContracts(String uuid, List<MultipartFile> images) {
    Optional<Groomer> optParent = groomerDAO.getByUuid(uuid);

    if (!optParent.isPresent()) {
      throw new ApiException("Unable to upload contract(s)", "Groomer not found for uuid=" + uuid);
    }

    FileValidatorUtils.validateUploadContracts(images);

    return optParent.get();
  }

  @Override
  public void validateSearch(GroomerSearchParamsDTO filters) {
    List<CustomSort> sorts = filters.getSorts();

    if (sorts != null && sorts.size() > 0) {
      for (CustomSort sort : sorts) {
        if (!GroomerSearchSorting.exist(sort.getProperty())) {
          throw new ApiException(ApiError.DEFAULT_MSG, "sort not found, " + sort,
              "valid values: " + GroomerSearchSorting.sortings.toString());
        }
      }
    }

    Integer pageSize = filters.getPageSize();

    if (pageSize != null && pageSize > 1000) {
      throw new ApiException(ApiError.DEFAULT_MSG, "pageSize is too big,pageSize=" + pageSize);
    }

    /**
     * Care Services
     */
    Set<String> poochSizes = filters.getPoochSizes();

    if (poochSizes != null) {
      for (String poochSize : poochSizes) {
        if (!PoochSize.isValidSize(poochSize)) {
          throw new ApiException(ApiError.DEFAULT_MSG, "poochSize=" + poochSize + " is invalid",
              "valid poochSize values=" + PoochSize.sizes);
        }
      }
    }

    Set<String> careServiceNames = filters.getCareServiceNames();

    if (careServiceNames != null) {
      for (String careServiceName : careServiceNames) {
        if (groomerServiceTypeService.getByName(careServiceName) == null) {
          throw new ApiException("Invalid service name", "service name=" + careServiceName,
              "valid serviceNames=" + GroomerServiceTypeServiceImp.dict.values(),
              "care service name is case sensitive");
        }
      }
    }

    Double poochMinPrice = filters.getMinPrice();

    if (poochMinPrice != null && (0 > poochMinPrice || poochMinPrice > 1000000)) {

      throw new ApiException(ApiError.DEFAULT_MSG,
          "poochMinPrice=" + poochMinPrice + " is invalid");

    }

    Double poochMaxPrice = filters.getMaxPrice();

    if (poochMaxPrice != null && (0 > poochMaxPrice || poochMaxPrice > 1000000)) {

      throw new ApiException(ApiError.DEFAULT_MSG,
          "poochMaxPrice=" + poochMaxPrice + " is invalid");

    }

    if (poochMinPrice != null && poochMaxPrice != null && poochMinPrice > poochMaxPrice) {
      throw new ApiException("Min Price must be equal to or less than Max Price",
          "poochMinPrice=" + poochMinPrice + " is invalid");
    }

    Integer rating = filters.getRating();

    if (rating != null && (rating < 0 || rating > 1000)) {

      throw new ApiException("Invalid rating", "rating=" + rating);

    }

  }

  @Override
  public Groomer validateSettingsUpdate(SettingsUpdateDTO settingsUpdateDTO) {
    String uuid = settingsUpdateDTO.getUuid();

    Groomer groomer = groomerDAO.getByUuid(uuid).orElseThrow(
        () -> new ApiException("Groomer not found", "groomer not found for uuid=" + uuid));

    return groomer;
  }

  @Override
  public Groomer validateUpdateListing(GroomerUpdateListingDTO groomerUpdateListingDTO) {
    // TODO Auto-generated method stub
    return null;
  }

}
