package com.pooch.api.entity.parent;

import java.util.Optional;

public interface ParentDAO {

    Parent save(Parent petParent);

    Optional<Parent> getByUuid(String uuid);
}
