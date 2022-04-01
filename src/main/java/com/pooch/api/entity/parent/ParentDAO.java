package com.pooch.api.entity.parent;

import java.util.Optional;

import com.pooch.api.entity.groomer.Groomer;

public interface ParentDAO {

    Parent save(Parent petParent);

    Optional<Parent> getByUuid(String uuid);

    Optional<Parent> getByEmail(String email);
}
