package com.pooch.api.entity.booking.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.booking.BookingCostDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImp implements TransactionService {

    @Autowired
    private TransactionDAO transactionDAO;

    @Override
    public Transaction addBookingInitialPayment(Booking booking, BookingCostDetails costDetails) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BOOKING_INITIAL_PAYMENT);
        transaction.setBookingCost(costDetails.getBookingCost());
        transaction.setBookingFee(costDetails.getBookingFee());
        transaction.setStripeFee(costDetails.getStripeFee());
        transaction.setTotalChargeAtDropOffAmount(costDetails.getTotalChargeAtDropOffAmount());
        transaction.setTotalChargeNowAmount(costDetails.getTotalChargeNowAmount());

        return transactionDAO.save(transaction);
    }
}
