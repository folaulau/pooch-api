package com.pooch.api.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetCareDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private PetParentDTO      petParent;

    private Set<PetDTO>       pets;

    private PetSitterDTO      petSitter;

}
