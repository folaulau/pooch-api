package com.pooch.api.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            groomerUuid;
    private String            parentUuid;
    private String            description;
    private double            rating;

}
