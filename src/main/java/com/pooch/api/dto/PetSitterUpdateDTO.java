package com.pooch.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class PetSitterUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String            uuid;

    private String            firstName;

    private String            lastName;

    private String            email;

    private Boolean           emailVerified;

    private String            phoneNumber;

    private Boolean           phoneVerified;

    private Integer           rating;

    private Boolean           offeredPickUp;

    private Boolean           offeredDropOff;

    private Double            chargePerMile;

    private Long              numberOfOcupancy;

    private String            description;
}
