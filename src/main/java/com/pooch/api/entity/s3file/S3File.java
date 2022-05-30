package com.pooch.api.entity.s3file;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.S3File + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.S3File,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class S3File implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  /**
   * original name of the file
   */
  @Column(name = "file_name")
  private String fileName;

  @Column(name = "s3_key", nullable = false)
  private String s3key;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  @Column(name = "url", nullable = false)
  private String url;

  /**
   * if not set, by default it's true
   */
  @Column(name = "is_public", nullable = false)
  private Boolean isPublic;

  /**
   * optional
   */
  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "parent_id", nullable = true)
  private Parent parent;

  /**
   * optional
   */
  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "groomer_id", nullable = true)
  private Groomer groomer;

  @Enumerated(EnumType.STRING)
  @Column(name = "file_type")
  private FileType fileType;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  /**
   * Groomer main profile image
   */
  @Column(name = "main_profile_image")
  private boolean mainProfileImage;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public S3File(String fileName, String s3key, String url) {
    this(fileName, s3key, url, true);
  }

  public S3File(String fileName, String s3key, String url, Boolean isPublic) {
    this.fileName = fileName;
    this.s3key = s3key;
    this.url = url;
    this.isPublic = isPublic;
  }

  @PrePersist
  private void preCreate() {
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "s3file-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
    }

    if (null == isPublic) {
      this.isPublic = true;
    }
  }
}
