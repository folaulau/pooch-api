package com.pooch.api.entity.s3file;

import java.util.List;
import java.util.Optional;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;

public interface S3FileDAO {

  S3File save(S3File s3File);

  List<S3File> save(List<S3File> s3Files);

  boolean delete(S3File s3File);

  Optional<S3File> getByUuid(String uuid);

  List<S3File> getByGroomerId(Long groomerId);

  S3File setMainProfileImage(Groomer groomer, S3File s3File);

  S3File setMainProfileImage(Parent parent, S3File s3File);
  
  long countProfileImages(Parent parent);

  long countProfileImages(Groomer groomer);

  List<S3File> getByParentId(Long parentId);

  Optional<S3File> getGroomerProfileImage(Long groomerId);

  long getGroomerFileCount(FileType profileImage, Long id);

}
