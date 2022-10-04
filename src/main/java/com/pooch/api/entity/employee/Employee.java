package com.pooch.api.entity.employee;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Employee + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.Employee,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "email"),
        @Index(columnList = "phone_number"), @Index(columnList = "deleted")})
public class Employee implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @NotEmpty
  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "phone_number")
  private String phoneNumber;

  @JsonIgnoreProperties(value = {"employees"})
  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "employee_roles", joinColumns = {@JoinColumn(name = "employee_id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id")})
  private Set<Role> roles;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private EmployeeStatus status;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public void addRole(Role role) {
    if (this.roles == null) {
      this.roles = new HashSet<>();
    }
    this.roles.add(role);
  }

  public String getRoleAsString() {
    if (this.roles == null) {
      return null;
    }
    return this.roles.stream().findFirst().get().getUserType().name();
  }

  public boolean isActive() {
    return Optional.ofNullable(this.status).orElse(EmployeeStatus.NONE)
        .equals(EmployeeStatus.ACTIVE);
  }

  public String getFullName() {
    StringBuilder str = new StringBuilder();
    if (this.firstName != null && !this.firstName.isEmpty()) {
      str.append(this.firstName);
    }

    if (this.lastName != null && !this.lastName.isEmpty()) {
      if (!str.toString().isBlank()) {
        str.append(" ");
      }
      str.append(this.lastName);
    }

    return str.toString();
  }

  @PrePersist
  private void preCreate() {

    if (uuid == null) {
      this.uuid = "employee-" + UUID.randomUUID().toString();
    }
  }

}
