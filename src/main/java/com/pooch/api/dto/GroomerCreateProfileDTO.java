package com.pooch.api.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.validators.NotEmptyString;
import com.pooch.api.validators.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class GroomerCreateProfileDTO implements Serializable {

    private static final long         serialVersionUID = 1L;

    
    @NotNull(message = "uuid is required")
    private String                    uuid;
    
    @NotNull(message = "firstName is required")
    private String                    firstName;
    
    @NotNull(message = "lastName is required")
    private String                    lastName;

    @NotNull(message = "businessName is required")
    private String                    businessName;

    @NotNull(message = "phoneNumber is required")
    private Long                      phoneNumber;
    
    @NotEmpty(message = "careServices is required")
    private Set<CareServiceUpdateDTO> careServices;

    private AddressCreateUpdateDTO    address;

    public void addCareService(CareServiceUpdateDTO careService) {
        if (this.careServices == null || this.careServices.size() == 0) {
            this.careServices = new HashSet<>();
        }
        this.careServices.add(careService);
    }
}
