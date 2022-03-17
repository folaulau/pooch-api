package com.pooch.api.entity.pet;

import java.util.List;
import java.util.Set;

import com.pooch.api.dto.PetCreateDTO;
import com.pooch.api.dto.PetDTO;
import com.pooch.api.entity.petparent.PetParent;

public interface PetService {

    List<PetDTO> add(PetParent petParent, Set<PetCreateDTO> petCreateDTOs);
}
