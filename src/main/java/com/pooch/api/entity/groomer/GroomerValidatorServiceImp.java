package com.pooch.api.entity.groomer;

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
    public Groomer validateUpdateProfile(GroomerUpdateDTO petSitterUpdateDTO) {
        String uuid = petSitterUpdateDTO.getUuid();

        if (uuid == null || uuid.isEmpty()) {
            throw new ApiException(ApiError.FAILURE, "uuid is empty. uuid=" + uuid);
        }

        Optional<Groomer> optPetSitter = groomerDAO.getByUuid(uuid);

        if (!optPetSitter.isPresent()) {
            throw new ApiException(ApiError.FAILURE, "Groomer not found for uuid=" + uuid);
        }

        Groomer petSitter = optPetSitter.get();

        return petSitter;
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
                    throw new ApiException(ApiError.DEFAULT_MSG, "sort not found, " + sort, "valid values: " +GroomerSearchSorting.sortings.toString());
                }
            }
        }

    }

}
