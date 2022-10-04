package com.pooch.api.entity.employee;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class EmployeeDAOImp implements EmployeeDAO {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Override
  public Employee save(Employee employee) {
    return employeeRepository.saveAndFlush(employee);
  }

  @Override
  public Optional<Employee> getByEmail(String email) {
    return employeeRepository.findByEmail(email);
  }

}
