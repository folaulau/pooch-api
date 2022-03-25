package com.pooch.api.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroomerUuidDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            uuid;
}
