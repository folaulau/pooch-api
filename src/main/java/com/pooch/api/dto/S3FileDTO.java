package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class S3FileDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String uuid;

  private String fileName;

  private String url;

  private Boolean isPublic;

  private boolean deleted;

  private boolean mainProfileImage;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
