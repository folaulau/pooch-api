package com.pooch.api.entity.s3file;

import java.util.List;

public interface S3FileDAO {

    S3File save(S3File s3File);

    List<S3File> save(List<S3File> s3Files);

}
