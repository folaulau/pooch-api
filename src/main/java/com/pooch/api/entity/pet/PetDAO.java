package com.pooch.api.entity.pet;

import java.util.Optional;

public interface PetDAO {

    Pet save(Pet pet);

    Optional<Pet> getByUuid(String uuid);
    
}
