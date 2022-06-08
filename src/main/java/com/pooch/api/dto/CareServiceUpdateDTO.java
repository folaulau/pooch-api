package com.pooch.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.validators.NotNull;
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
    @NotNull(message = "smallPrice is required")
    private Double            smallPrice;

    private boolean           serviceSmall;

    /**
     * 21-40LB
     */
    @NotNull(message = "mediumPrice is required")
    private Double            mediumPrice;

    private boolean           serviceMedium;

    /**
     * 41LB +
     */
    @NotNull(message = "largePrice is required")
    private Double            largePrice;

    private boolean           serviceLarge;
}
