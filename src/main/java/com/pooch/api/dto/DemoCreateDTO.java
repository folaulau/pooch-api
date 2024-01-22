package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Min;
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
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.entity.role.Role;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class DemoCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "firstName must not be empty")
    private String            firstName;

    @NotEmpty(message = "lastName must not be empty")
    private String            lastName;

    @NotEmpty(message = "email must not be empty")
    private String            email;

    @NotEmpty(message = "phoneNumber must not be empty")
    private String            phoneNumber;

    @NotEmpty(message = "companyName must not be empty")
    private String            companyName;

    private String            companyWebsite;
    
    @NotEmpty(message = "numberOfPetsPerDay must not be empty")
    private String           numberOfPetsPerDay;

    @NotEmpty(message = "services must not be empty")
    private Set<String>       services;

    private boolean           marketingCommunicationConsent;

}
