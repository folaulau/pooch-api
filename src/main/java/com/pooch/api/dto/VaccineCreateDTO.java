package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineCreateDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private LocalDateTime     expireDate;

    private String            name;
}
