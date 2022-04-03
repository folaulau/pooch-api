package com.pooch.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private Double            longitude;
    private Integer           radius;
    private Integer           pageNumber;
    private Integer           pageSize;
    private String            searchPhrase;
    private List<String>      sorts;

    public void addSort(String sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(sort);
    }
}
