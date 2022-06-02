package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @NotEmpty(message = "uuid is required")
  private String uuid;

  private String reason;

}
