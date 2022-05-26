package com.pooch.api.entity.chat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.dto.ReviewCreateDTO;
import com.pooch.api.dto.S3FileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_NULL)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  /**
   * firebase uuid
   */
  private String uuid;


  /**
   * type=TEXT
   */
  private String message;

  /**
   * type=DOC
   */
  private List<ChatDocument> documents;

  private String senderUserType;

  // LocalDateTime
  private String groomerViewedAt;

  // LocalDateTime
  private String parentViewedAt;

  private boolean delete;

  private String type;

  private String receiverUserType;

  private String createdAt;

  private String updateedAt;
}
