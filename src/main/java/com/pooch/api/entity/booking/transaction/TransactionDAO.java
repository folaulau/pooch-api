package com.pooch.api.entity.booking.transaction;

import java.util.List;

public interface TransactionDAO {

    Transaction save(Transaction transaction);

    List<Transaction> getByBookingId(Long bookingId);
}
