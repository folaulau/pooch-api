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
public class PetCareCreateDTO implements Serializable {

    /**
     * 
     */
    private static final long  serialVersionUID = 1L;

    private String             petSitterUuid;

    private PetParentUpdateDTO petParent;

    private Set<PetCreateDTO>  pets;

    private LocalDateTime      pickUpDateTime;

    private LocalDateTime      dropOffDateTime;

    private LocalDateTime      startDateTime;

    private LocalDateTime      endDateTime;

}
