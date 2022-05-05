package com.pooch.api.entity.s3file;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface S3FileRepository extends JpaRepository<S3File, Long> {

    Optional<S3File> findByUuid(String uuid);
    
   List<S3File> findByParentId(Long parentId);
   
   List<S3File> findByGroomerId(Long groomerId);
}
