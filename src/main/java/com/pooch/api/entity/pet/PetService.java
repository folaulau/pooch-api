package com.pooch.api.entity.pet;

import java.util.List;
import java.util.Set;

import com.pooch.api.dto.PetCreateDTO;
import com.pooch.api.dto.PetDTO;
import com.pooch.api.entity.parent.Parent;

public interface PetService {

    List<PetDTO> add(Parent petParent, Set<PetCreateDTO> petCreateDTOs);
}
