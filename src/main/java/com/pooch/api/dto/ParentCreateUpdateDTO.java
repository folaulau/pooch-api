package com.pooch.api.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
