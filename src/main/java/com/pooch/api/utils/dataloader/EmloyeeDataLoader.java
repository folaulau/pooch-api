package com.pooch.api.utils.dataloader;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.booking.BookingRepository;
import com.pooch.api.entity.booking.BookingStatus;
import com.pooch.api.entity.employee.Employee;
import com.pooch.api.entity.employee.EmployeeDAO;
import com.pooch.api.entity.employee.EmployeeStatus;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochRepository;
import com.pooch.api.entity.role.Role;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.utils.PasswordUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"local"})
@Component
public class EmloyeeDataLoader implements ApplicationRunner {

  @Autowired
  private EmployeeDAO employeeDAO;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    // 1
    Employee employee = Employee.builder().id(1L).email("folaukaveinga@gmail.com")
        .password(PasswordUtils.hashPassword("Poochadmin1234!")).firstName("Folau")
        .lastName("Kaveinga").phoneNumber("3109934731").status(EmployeeStatus.ACTIVE).build();

    employee.addRole(new Role(UserType.admin));

    employeeDAO.save(employee);

    employee = Employee.builder().id(2L).email("poochadmin@gmail.com")
        .password(PasswordUtils.hashPassword("Poochadmin1234!")).firstName("Pooch")
        .lastName("Admin").phoneNumber("3109934731").status(EmployeeStatus.ACTIVE).build();

    employee.addRole(new Role(UserType.admin));

    employeeDAO.save(employee);

  }

}
