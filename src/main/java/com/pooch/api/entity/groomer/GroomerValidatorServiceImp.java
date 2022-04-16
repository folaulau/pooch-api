package com.pooch.api.entity.groomer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.dto.CustomSort;
import com.pooch.api.dto.GroomerSearchParamsDTO;
import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.utils.FileValidatorUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerValidatorServiceImp implements GroomerValidatorService {

    @Autowired
    private GroomerDAO groomerDAO;

    @Override
    public Groomer validateUpdateProfile(GroomerUpdateDTO groomerUpdateDTO) {
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
            GroomerSignUpStatus k = Optional.ofNullable(groomerUpdateDTO.getSignUpStatus())
                    .orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "signUpStatus is required", "status=" + Arrays.asList(GroomerSignUpStatus.values())));

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
