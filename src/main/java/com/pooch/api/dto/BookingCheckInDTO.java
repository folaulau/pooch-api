package com.pooch.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.validators.NotEmptyString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@JsonInclude(value = Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCheckInDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @NotEmptyString(message = "uuid is required")
  private String uuid;

  @NotEmptyString(message = "groomerUuid is required")
  private String groomerUuid;

}
