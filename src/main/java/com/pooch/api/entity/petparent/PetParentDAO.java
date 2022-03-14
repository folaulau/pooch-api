package com.pooch.api.entity.petparent;

import java.util.Optional;

public interface PetParentDAO {

    PetParent save(PetParent petParent);

    Optional<PetParent> getByUuid(String uuid);
}
