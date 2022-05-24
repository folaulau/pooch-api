package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCareServiceDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            uuid;

    /**
     * PoochSize, Small, Medium, or Large
     */
    private String            size;
}
