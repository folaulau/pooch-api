package com.pooch.api.dto;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.Type;

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

    private static final long         serialVersionUID = 1L;

    private String                    uuid;

    private String                    firstName;

    private String                    lastName;

    private String                    businessName;

    private String                    email;

    private boolean                   emailTemp;

    private Long                      phoneNumber;

    private Integer                   rating;

    private Boolean                   offeredPickUp;

    private Boolean                   offeredDropOff;

    private Double                    chargePerMile;

    private Long                      numberOfOcupancy;

    private String                    description;

    private Boolean                   instantBooking;

    private Set<CareServiceUpdateDTO> careServices;
}
