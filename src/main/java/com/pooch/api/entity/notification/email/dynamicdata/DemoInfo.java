package com.pooch.api.entity.notification.email.dynamicdata;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class DemoInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String            firstName;

    private String            lastName;

    private String            email;

    private String            phoneNumber;

    private String            companyName;

    private String            companyWebsite;

    private String            numberOfPetsPerDay;

    private String            services;

    private String           marketingCommunicationConsent;//yes or no

}
