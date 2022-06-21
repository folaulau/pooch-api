package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.validators.Email;
import com.pooch.api.validators.NotNull;
import com.pooch.api.validators.Password;
import com.pooch.api.validators.USPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class SettingsUpdateDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull(message = "uuid is required")
  private String uuid;

  @Email(message = "email is invalid")
  private String email;

  @Password(message = "password does not meet requirements")
  private String password;

  @NotNull(message = "firstName is required")
  private String firstName;

  @NotNull(message = "lastName is required")
  private String lastName;

  @NotNull(message = "businessName is required")
  private String businessName;

  @Positive(message = "countryCode is required")
  private Integer countryCode;

  @USPhoneNumber(message = "phoneNumber is invalid")
  private Long phoneNumber;

  private AddressCreateUpdateDTO address;
}
