package com.pooch.api.entity.petsitter;

import java.util.Optional;

public interface PetSitterDAO {

    PetSitter save(PetSitter petSitter);

    Optional<PetSitter> getById(long id);

    Optional<PetSitter> getByUuid(String uuid);
}
