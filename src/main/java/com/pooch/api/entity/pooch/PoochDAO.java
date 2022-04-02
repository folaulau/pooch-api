package com.pooch.api.entity.pooch;

import java.util.Optional;

public interface PoochDAO {

    Pooch save(Pooch pet);

    Optional<Pooch> getByUuid(String uuid);
    
}
