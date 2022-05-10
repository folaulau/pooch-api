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
public class GroomerCreateListingDTO implements Serializable {

    private static final long         serialVersionUID = 1L;

    private String                    uuid;

    private Boolean                   offeredPickUp;

    private Boolean                   offeredDropOff;

    private Double                    chargePerMile;

    private Long                      numberOfOcupancy;

    private String                    description;

    private Boolean                   instantBooking;

    private Set<CareServiceUpdateDTO> careServices;

    public void addCareService(CareServiceUpdateDTO careService) {
        if (this.careServices == null || this.careServices.size() == 0) {
            this.careServices = new HashSet<>();
        }
        this.careServices.add(careService);
    }
}
