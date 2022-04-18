package com.pooch.api.entity.address;

import java.util.Optional;

public interface AddressDAO {

    Optional<Address> getByUuid(String uuid);
}
