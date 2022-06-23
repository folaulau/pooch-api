package com.pooch.api.entity.s3file;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class S3FileDAOImp implements S3FileDAO {

  @Autowired
  private S3FileRepository s3FileRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public S3File save(S3File s3File) {
    return s3FileRepository.saveAndFlush(s3File);
  }

  @Override
  public List<S3File> save(List<S3File> s3Files) {

    s3Files = s3FileRepository.saveAll(s3Files);
    s3FileRepository.flush();

    return s3Files;
  }

  @Override
  public boolean delete(S3File s3File) {
    s3File.setDeleted(true);
    try {
      this.save(s3File);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  @Override
  public Optional<S3File> getByUuid(String uuid) {
    return s3FileRepository.findByUuid(uuid);
  }

  @Override
  public List<S3File> getByGroomerId(Long groomerId) {
    return s3FileRepository.findByGroomerId(groomerId);
  }

  @Override
  public List<S3File> getByParentId(Long parentId) {
    return s3FileRepository.findByParentId(parentId);
  }

  @Override
  public Optional<S3File> getGroomerProfileImage(Long groomerId) {
    return s3FileRepository
        .findByGroomerIdAndFileTypeAndMainProfileImage(groomerId, FileType.PROFILE_IMAGE, true)
        .stream().findFirst();
  }

  @Override
  public S3File setMainProfileImage(Groomer groomer, S3File s3File) {

    StringBuilder query = new StringBuilder();

    query.append("UPDATE s3file ");
    query.append("SET main_profile_image = false ");
    query.append("WHERE groomer_id = ? AND deleted = false ");
    query.append("AND file_type = ");
    query.append("'" + FileType.PROFILE_IMAGE.name() + "'");

    log.info("query={}", query.toString());

    Integer numOfUpdates = null;

    try {
      numOfUpdates = jdbcTemplate.update(query.toString(), new Object[] {groomer.getId()});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getMessage());
    }

    s3File.setMainProfileImage(true);

    return this.save(s3File);
  }

  @Override
  public S3File setMainProfileImage(Parent parent, S3File s3File) {
    StringBuilder query = new StringBuilder();

    query.append("UPDATE s3file ");
    query.append("SET main_profile_image = false ");
    query.append("WHERE parent_id = ? AND deleted = false ");
    query.append("AND file_type = ");
    query.append("'" + FileType.PROFILE_IMAGE.name() + "'");

    log.info("query={}", query.toString());

    Integer numOfUpdates = null;

    try {
      numOfUpdates = jdbcTemplate.update(query.toString(), new Object[] {parent.getId()});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getMessage());
    }

    s3File.setMainProfileImage(true);

    return this.save(s3File);
  }

  @Override
  public long getGroomerFileCount(FileType profileImage, Long groomerId) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT COUNT(id) as totalCount ");
    query.append("FROM s3file ");
    query.append("WHERE parent_id = ? AND deleted = false ");
    query.append("AND file_type = ");
    query.append("'" + profileImage.name() + "'");

    log.info("query={}", query.toString());

    long count = 0;

    try {
      count = jdbcTemplate.queryForObject(query.toString(), Long.class, new Object[] {groomerId});
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getMessage());
    }

    return count;
  }

  @Override
  public long countProfileImages(Parent parent) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT COUNT(id) as imageCount ");
    query.append("FROM s3file ");
    query.append("WHERE parent_id = ? AND deleted = false ");
    query.append("AND main_profile_image = true AND file_type = ");
    query.append("'" + FileType.PROFILE_IMAGE.name() + "'");

    log.info("query={}", query.toString());

    long imageCount = 0;

    try {
      imageCount = jdbcTemplate.queryForObject(query.toString(), Long.class, new Object[] {parent.getId()});
   
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getMessage());
    }
    
    return imageCount;
  }

  @Override
  public long countProfileImages(Groomer groomer) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT COUNT(id) as imageCount ");
    query.append("FROM s3file ");
    query.append("WHERE groomer_id = ? AND deleted = false ");
    query.append("AND main_profile_image = true AND file_type = ");
    query.append("'" + FileType.PROFILE_IMAGE.name() + "'");

    log.info("query={}", query.toString());

    long imageCount = 0;

    try {
      imageCount = jdbcTemplate.queryForObject(query.toString(), Long.class, new Object[] {groomer.getId()});
   
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getMessage());
    }
    
    return imageCount;
  }
}
