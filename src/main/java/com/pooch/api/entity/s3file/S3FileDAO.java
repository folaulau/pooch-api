package com.pooch.api.entity.s3file;

import java.util.List;
import java.util.Optional;

public interface S3FileDAO {

    S3File save(S3File s3File);

    List<S3File> save(List<S3File> s3Files);

    boolean delete(S3File s3File);

    Optional<S3File> getByUuid(String uuid);

    List<S3File> getByGroomerId(Long groomerId);

    List<S3File> getByParentId(Long parentId);

}
