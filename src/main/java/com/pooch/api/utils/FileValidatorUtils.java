package com.pooch.api.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.pooch.api.exception.ApiException;

public interface FileValidatorUtils {

    /**
     * limit to 10 MB
     */
    long                FILE_SIZE_LIMIT        = 10485760;

    /**
     * https://developer.mozilla.org/en-US/docs/Web/Media/Formats/Image_types
     */
    Map<String, String> VALID_IMAGE_EXTENSIONS = Arrays.asList("tiff", "tif", "png", "gif", "jpeg", "jpg", "jpe", "jfi", "jfif", "pjpeg", "pjp", "svg", "svgz")
            .stream()
            .collect(Collectors.toMap(Function.identity(), Function.identity()));

    public static void validateUploadImages(List<MultipartFile> images) {
        if (images.size() > 100) {
            throw new ApiException("Too many images. Limit to 100");
        }

        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);

            if (null == file || file.isEmpty()) {
                throw new ApiException("Unable to upload image", "file #" + (i + 1) + " is empty");
            }

            String originalFilename = file.getOriginalFilename();

            if (file.getSize() > FILE_SIZE_LIMIT) {
                throw new ApiException(originalFilename + " is too big. 10MB is the limit");
            }

            String extension = FileUtils.getFileExtension(originalFilename).toLowerCase();

            if (!VALID_IMAGE_EXTENSIONS.containsKey(extension)) {
                throw new ApiException(originalFilename + " is invalid. Unsupported file extension");
            }
        }
    }
}
