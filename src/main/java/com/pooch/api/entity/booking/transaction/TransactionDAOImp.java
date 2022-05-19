package com.pooch.api.entity.booking.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class TransactionDAOImp implements TransactionDAO {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.saveAndFlush(transaction);
    }

    @Override
    public List<Transaction> getByBookingId(Long bookingId) {
        return transactionRepository.findByBookingId(bookingId);
    }
}
