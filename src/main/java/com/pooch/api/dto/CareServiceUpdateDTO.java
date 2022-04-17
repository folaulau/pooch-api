package com.pooch.api.dto;

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
public class CareServiceUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String            uuid;

    private String            name;

    /**
     * 1-20LB
     */
    private Double            smallPrice;

    private boolean           serviceSmall;

    /**
     * 21-40LB
     */
    private Double            mediumPrice;

    private boolean           serviceMedium;

    /**
     * 41LB +
     */
    private Double            largePrice;

    private boolean           serviceLarge;
}
