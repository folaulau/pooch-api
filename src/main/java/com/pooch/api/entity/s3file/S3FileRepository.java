package com.pooch.api.entity.s3file;

import org.springframework.data.jpa.repository.JpaRepository;

public interface S3FileRepository extends JpaRepository<S3File, Long> {

}
