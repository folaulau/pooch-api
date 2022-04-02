package com.pooch.api.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroomerSearchFiltersDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Double            latitude;
    private Double            longtitude;
    private Integer           radius;
    private Integer           pageNumber;
    private Integer           pageSize;
    private String            searchPhrase;

}
