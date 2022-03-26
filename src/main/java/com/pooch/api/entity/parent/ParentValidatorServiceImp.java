package com.pooch.api.entity.parent;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.exception.ApiException;
import com.pooch.api.utils.FileValidatorUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParentValidatorServiceImp implements ParentValidatorService {

    @Autowired
    private ParentDAO parentDAO;

    @Override
    public Parent validateUploadProfileImages(String uuid, List<MultipartFile> images) {

        Optional<Parent> optParent = parentDAO.getByUuid(uuid);

        if (!optParent.isPresent()) {
            throw new ApiException("Unable to upload image(s)", "Parent not found for uuid=" + uuid);
        }

        FileValidatorUtils.validateUploadImages(images);

        return optParent.get();
    }

}
