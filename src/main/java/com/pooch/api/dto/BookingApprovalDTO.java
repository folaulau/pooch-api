package com.pooch.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.validators.NotEmptyString;
import com.pooch.api.validators.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@JsonInclude(value = Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingApprovalDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @NotEmptyString(message = "uuid is required")
  private String uuid;

  @NotNull(message = "approved is required")
  private Boolean approved;

  private String note;

}
