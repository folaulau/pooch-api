package com.pooch.api.entity.parent;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ParentValidatorService {

    Parent validateUploadProfileImages(String uuid, List<MultipartFile> images);
}
