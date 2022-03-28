package com.pooch.api.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class GroomerUpdateDTO implements Serializable {

    private static final long           serialVersionUID = 1L;

    private String                      uuid;

    private String                      firstName;

    private String                      lastName;

    private String                      businessName;

    private String                      email;

    private boolean                     emailTemp;

    private Long                        phoneNumber;

    private Boolean                     offeredPickUp;

    private Boolean                     offeredDropOff;

    private Double                      chargePerMile;

    private Long                        numberOfOcupancy;

    private String                      description;

    private Boolean                     instantBooking;

    private Set<CareServiceUpdateDTO>   careServices;

    private Set<AddressCreateUpdateDTO> addresses;

    public void addCareService(CareServiceUpdateDTO careService) {
        if (this.careServices == null || this.careServices.size() == 0) {
            this.careServices = new HashSet<>();
        }
        this.careServices.add(careService);
    }

    public void addAddress(AddressCreateUpdateDTO address) {
        if (this.addresses == null || this.addresses.size() == 0) {
            this.addresses = new HashSet<>();
        }
        this.addresses.add(address);
    }
}
