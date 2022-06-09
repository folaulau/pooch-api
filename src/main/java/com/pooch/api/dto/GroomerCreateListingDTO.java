package com.pooch.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.validators.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class GroomerCreateListingDTO implements Serializable {

    private static final long         serialVersionUID = 1L;

    @NotNull(message = "uuid is required")
    private String                    uuid;

    private Boolean                   offeredPickUp;

    private Boolean                   offeredDropOff;

    private Double                    chargePerMile;

    @NotNull(message = "numberOfOccupancy is required")
    private Long                      numberOfOccupancy;

    private String                    description;

    private Boolean                   instantBooking;

    private List<@javax.validation.constraints.NotNull @Valid CareServiceUpdateDTO> careServices;

    public void addCareService(CareServiceUpdateDTO careService) {
        if (this.careServices == null || this.careServices.size() == 0) {
            this.careServices = new ArrayList<>();
        }
        this.careServices.add(careService);
    }
}
