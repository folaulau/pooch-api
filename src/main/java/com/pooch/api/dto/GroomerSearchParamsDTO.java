package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Groomer search filters and sorts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroomerSearchParamsDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Double            latitude;
    private Double            longitude;
    private Integer           distance;
    private Integer           pageNumber;
    private Integer           pageSize;
    private String            searchPhrase;
    private List<CustomSort>  sorts;

    private LocalDate         startDate;
    private LocalDate         endDate;
    private List<String>      dogSizes;
    private List<String>      careServices;

    // review rating of groomers
    private Integer           rating;

    public void addSort(String sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(new CustomSort(sort, CustomSort.ASC));
    }
}
