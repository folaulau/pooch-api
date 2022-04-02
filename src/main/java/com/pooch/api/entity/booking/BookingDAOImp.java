package com.pooch.api.entity.booking;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BookingDAOImp implements BookingDAO {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Optional<Booking> getByUuid(String uuid) {
        // TODO Auto-generated method stub
        return bookingRepository.findByUuid(uuid);
    }
}
