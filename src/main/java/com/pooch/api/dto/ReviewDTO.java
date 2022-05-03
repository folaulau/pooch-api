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
public class ReviewDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long              groomerId;
    private Long              parentId;
    private String            description;
    private double            rating;

}
