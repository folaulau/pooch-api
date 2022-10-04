package com.pooch.api.entity.employee;

import static org.springframework.http.HttpStatus.OK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.AuthenticatorUsernamePasswordDTO;
import com.pooch.api.entity.parent.ParentRestController;
import com.pooch.api.utils.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Employees", description = "Employee Operations")
@RestController
@RequestMapping("/employees")
public class EmployeeRestController {

  @Autowired
  private EmployeeService employeeService;

  @Operation(summary = "Authenticate", description = "sign up or sign in")
  @PostMapping(value = "/signin")
  public ResponseEntity<AuthenticationResponseDTO> authenticate(
      @RequestHeader(name = "x-api-key", required = true) String xApiKey,
      @RequestBody AuthenticatorUsernamePasswordDTO authenticatorDTO) {
    log.info("authenticate={}", ObjectUtils.toJson(authenticatorDTO));

    AuthenticationResponseDTO authenticationResponseDTO = employeeService.signin(authenticatorDTO);

    return new ResponseEntity<>(authenticationResponseDTO, OK);
  }
}
