package com.pooch.api.entity.booking;

import java.util.Optional;

public interface BookingDAO {

    Optional<Booking> getByUuid(String uuid);

}
