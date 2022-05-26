package com.pooch.api.entity.chat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.pooch.api.dto.S3FileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDocument implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String url;

  private String fileType;

}
