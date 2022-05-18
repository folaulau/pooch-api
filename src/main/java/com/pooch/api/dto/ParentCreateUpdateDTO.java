package com.pooch.api.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@JsonInclude(value = Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentCreateUpdateDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            uuid;

    private String            fullName;

    private String            email;

    private Integer           countryCode;

    private Long              phoneNumber;

    // https://www.twilio.com/blog/best-practices-phone-number-validation-user-enrollment
    private String            phoneNumberVerificationUuid;

}
