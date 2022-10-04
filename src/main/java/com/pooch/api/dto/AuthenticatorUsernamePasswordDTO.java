package com.pooch.api.dto;

import java.io.Serializable;
import com.pooch.api.validators.Email;
import com.pooch.api.validators.Password;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatorUsernamePasswordDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Email(message = "Invalid email")
  private String email;

  @Password(message = "Invalid password")
  private String password;

}
