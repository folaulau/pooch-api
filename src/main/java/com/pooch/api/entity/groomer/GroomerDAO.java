package com.pooch.api.entity.groomer;

import java.util.Optional;

public interface GroomerDAO {

    Groomer save(Groomer groomer);

    Optional<Groomer> getById(long id);

    Optional<Groomer> getByUuid(String uuid);
}
