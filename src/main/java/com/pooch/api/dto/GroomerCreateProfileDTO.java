package com.pooch.api.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class GroomerCreateProfileDTO implements Serializable {

    private static final long         serialVersionUID = 1L;

    private String                    uuid;

    private String                    firstName;

    private String                    lastName;

    private String                    businessName;

    private Long                      phoneNumber;

    private GroomerSignUpStatus       signUpStatus;

    private Set<CareServiceUpdateDTO> careServices;

    private AddressCreateUpdateDTO    address;

    public void addCareService(CareServiceUpdateDTO careService) {
        if (this.careServices == null || this.careServices.size() == 0) {
            this.careServices = new HashSet<>();
        }
        this.careServices.add(careService);
    }
}
