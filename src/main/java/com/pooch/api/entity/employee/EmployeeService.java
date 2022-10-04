package com.pooch.api.entity.employee;

import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorUsernamePasswordDTO;

public interface EmployeeService {

  AuthenticationResponseDTO signin(AuthenticatorUsernamePasswordDTO authenticatorDTO);

}
