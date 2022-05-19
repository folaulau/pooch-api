package com.pooch.api.entity.booking.transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBookingId(Long bookingId);
}
