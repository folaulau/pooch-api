package com.pooch.api.entity.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorUsernamePasswordDTO;
import com.pooch.api.exception.ApiException;
import com.pooch.api.security.AuthenticationService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImp implements EmployeeService {

  @Autowired
  private EmployeeDAO employeeDAO;

  @Autowired
  private AuthenticationService authenticationService;

  @Override
  public AuthenticationResponseDTO signin(AuthenticatorUsernamePasswordDTO authenticatorDTO) {

    String email = authenticatorDTO.getEmail();

    Employee employee = employeeDAO.getByEmail(email)
        .orElseThrow(() -> new ApiException("Employee not found", "Employee not found by email"));

    if (!employee.getStatus().equals(EmployeeStatus.ACTIVE)) {
      throw new ApiException("Employee not active", "Employee's status not active");
    }


    if (!PasswordUtils.verify(authenticatorDTO.getPassword(), employee.getPassword())) {
      throw new ApiException("Invalid password", "Invalid password");
    }


    AuthenticationResponseDTO authenticationResponseDTO =
        authenticationService.authenticate(employee);

    log.info("authenticationResponseDTO={}", ObjectUtils.toJson(authenticationResponseDTO));

    return authenticationResponseDTO;
  }


}
