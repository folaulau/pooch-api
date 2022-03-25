package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class GroomerDTO implements Serializable {

    private static final long      serialVersionUID = 1L;

    private Long                   id;

    private String                 uuid;

    private String                 firstName;

    private String                 lastName;

    private String                 businessName;

    private String                 email;

    private Boolean                emailVerified;

    private boolean                emailTemp;

    private Long                   phoneNumber;

    private Boolean                phoneNumberVerified;

    private Integer                rating;

    private Boolean                offeredPickUp;

    private Boolean                offeredDropOff;

    private Double                 chargePerMile;

    private Long                   numberOfOcupancy;

    private String                 description;

    private boolean                instantBooking;

    private Set<CareServiceDTO> careServices;

    private LocalDateTime          createdAt;

    private LocalDateTime          updatedAt;

}
