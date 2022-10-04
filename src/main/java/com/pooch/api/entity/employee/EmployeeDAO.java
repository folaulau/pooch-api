package com.pooch.api.entity.employee;

import java.util.Optional;

public interface EmployeeDAO {

  Employee save(Employee employee);

  Optional<Employee> getByEmail(String email);
}
