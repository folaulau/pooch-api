package com.pooch.api.entity.groomer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface GroomerDAO {

    Groomer save(Groomer groomer);

    Optional<Groomer> getById(long id);

    Optional<Groomer> getByUuid(String uuid);

    Optional<Groomer> getByEmail(String email);

    boolean existEmail(String email);
    
    void updateRating(long id);
}
