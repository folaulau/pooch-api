package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

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
    private List<CustomSort>  sortings;
    private List<String>      sorts;

    private LocalDateTime     startDateTime;
    private LocalDateTime     endDateTime;
    private List<String>      dogSizes;
    private List<String>      careServices;

    // review rating of groomers
    private Integer           reviewRating;

    public void addSort(String sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(sort);
    }
}
