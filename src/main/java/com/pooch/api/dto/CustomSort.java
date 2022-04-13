package com.pooch.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomSort implements Serializable {

    private static final long  serialVersionUID = 1L;

    public static final String ASC              = "asc";
    public static final String DESC             = "desc";

    /**
     * name of the property
     */
    private String             property;

    /**
     * DESC or ASC
     */
    private String             direction;
    
    public CustomSort(String property) {
        this(property,ASC);
    }
}
