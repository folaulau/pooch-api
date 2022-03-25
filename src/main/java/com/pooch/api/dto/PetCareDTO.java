package com.pooch.api.dto;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_NULL)
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

    private GroomerDTO      petSitter;

}
