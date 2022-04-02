package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            uuid;

    private String            reason;

}
