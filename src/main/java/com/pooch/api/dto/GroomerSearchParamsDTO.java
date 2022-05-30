package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.pooch.api.entity.pooch.PoochSize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Groomer search filters and sorts
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroomerSearchParamsDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * filters
     */
    private Double            latitude;
    private Double            longitude;
    // filter and sort
    private Integer           distance;
    private Integer           pageNumber;
    private Integer           pageSize;
    private String            searchPhrase;

    /**
     * Calendar
     */
    private LocalDate         startDate;
    private LocalDate         endDate;

    /**
     * Care Service
     */
    private Set<String>       poochSizes;
    private Set<String>       careServiceNames;
    private Double            minPrice;
    private Double            maxPrice;

    /**
     * Rating
     */
    private Integer           rating;

    /**
     * sort
     */
    private List<CustomSort>  sorts;

    public void addSort(String sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(new CustomSort(sort, CustomSort.ASC));
    }
}
