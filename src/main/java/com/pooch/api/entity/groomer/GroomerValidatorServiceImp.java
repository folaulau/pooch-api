package com.pooch.api.entity.groomer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.CareServiceUpdateDTO;
import com.pooch.api.dto.CustomSort;
import com.pooch.api.dto.GroomerSearchParamsDTO;
import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.groomer.careservice.CareServiceName;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.utils.FileValidatorUtils;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerValidatorServiceImp implements GroomerValidatorService {

    @Autowired
    private GroomerDAO     groomerDAO;

    @Autowired
    private CareServiceDAO careServiceDAO;

    @Override
    public Groomer validateUpdateProfile(GroomerUpdateDTO groomerUpdateDTO) {

        log.info("groomerUpdateDTO={}", ObjectUtils.toJson(groomerUpdateDTO));

        String uuid = groomerUpdateDTO.getUuid();

        if (uuid == null || uuid.isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "uuid is empty. uuid=" + uuid);
        }

        Optional<Groomer> optGroomer = groomerDAO.getByUuid(uuid);

        if (!optGroomer.isPresent()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "Groomer not found for uuid=" + uuid);
        }

        Groomer groomer = optGroomer.get();

        Optional.ofNullable(groomerUpdateDTO.getEmail()).ifPresent(email -> {
            if (!email.trim().equalsIgnoreCase(groomer.getEmail())) {
                if (groomerDAO.existEmail(email)) {
                    log.debug("email is taken");
                    throw new ApiException("Email is taken");
                }
            }
        });

        if (groomer.getStatus().equals(GroomerStatus.SIGNING_UP)) {
            GroomerSignUpStatus signUpStatus = Optional.ofNullable(groomerUpdateDTO.getSignUpStatus())
                    .orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "signUpStatus is required", "status=" + Arrays.asList(GroomerSignUpStatus.values())));

        }

        Set<CareServiceUpdateDTO> careServicesDTOs = groomerUpdateDTO.getCareServices();

        if (careServicesDTOs != null) {
            Set<String> serviceNames = new HashSet<>();

            for (CareServiceUpdateDTO careServiceUpdateDTO : careServicesDTOs) {

                final String serviceName = careServiceUpdateDTO.getName();

                /**
                 * validate service name
                 */
                if (serviceName == null || serviceName.isBlank()) {
                    throw new ApiException("Service name is required", "serviceName=" + serviceName);
                }

                if (!CareServiceName.isValidCareServiceName(serviceName)) {
                    throw new ApiException("Invalid service name", "service name=" + serviceName, "valid serviceNames=" + CareServiceName.careServiceNames, "care service name is case sensitive");
                }

                if (serviceNames.contains(serviceName)) {
                    throw new ApiException(serviceName + " is a duplicate", "serviceName=" + serviceName);
                }

                /**
                 * validate prices
                 */
                if (careServiceUpdateDTO.isServiceSmall()) {
                    Double smallPrice = careServiceUpdateDTO.getSmallPrice();
                    /**
                     * assume 10 million must be a mistake
                     */
                    if (smallPrice == null || smallPrice <= -1 || smallPrice >= 10000000) {
                        throw new ApiException(serviceName + ", smallPrice is valid", "smallPrice=" + smallPrice);
                    }
                }

                if (careServiceUpdateDTO.isServiceMedium()) {
                    Double mediumPrice = careServiceUpdateDTO.getSmallPrice();
                    /**
                     * assume 10 million must be a mistake
                     */
                    if (mediumPrice == null || mediumPrice <= -1 || mediumPrice >= 10000000) {
                        throw new ApiException(serviceName + ", mediumPrice is valid", "mediumPrice=" + mediumPrice);
                    }
                }

                if (careServiceUpdateDTO.isServiceLarge()) {
                    Double largePrice = careServiceUpdateDTO.getSmallPrice();
                    /**
                     * assume 10 million must be a mistake
                     */
                    if (largePrice == null || largePrice <= -1 || largePrice >= 10000000) {
                        throw new ApiException(serviceName + ", largePrice is valid", "largePrice=" + largePrice);
                    }
                }

                /**
                 * validate updating existing care service
                 */
                Optional.ofNullable(careServiceUpdateDTO.getUuid()).ifPresent(careServiceUuid -> {
                    if (!careServiceDAO.existByUuidAndGroomer(careServiceUuid, groomer.getId())) {
                        throw new ApiException(ApiError.DEFAULT_MSG, "uuid not found for this care service", "make sure you pass the correct uuid for careService");
                    }
                });

                serviceNames.add(serviceName);
            }
        }

        return groomer;
    }

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
                    throw new ApiException(ApiError.DEFAULT_MSG, "sort not found, " + sort, "valid values: " + GroomerSearchSorting.sortings.toString());
                }
            }
        }

    }


}
