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
    transaction.setTotalChargeAtDropOff(costDetails.getTotalChargeAtDropOff());
    transaction.setTotalChargeAtBooking(costDetails.getTotalChargeAtBooking());
    transaction.setBooking(booking);

    return transactionDAO.save(transaction);
  }


  @Override
  public Transaction addBookingCancellation(Booking booking, Double refundedAmount,
      Double nonRefundedAmount) {
    /**
     * - cancelUserType, cancelUserId, refundedAmount, nonRefundedAmount, description
     */
    Transaction transaction = new Transaction();
    transaction.setType(TransactionType.BOOKING_CANCELLATION);
    transaction.setCancelUserType(booking.getCancelUserType());
    transaction.setCancelUserId(booking.getCancelUserId());
    transaction.setRefundedAmount(nonRefundedAmount);
    transaction.setNonRefundedAmount(nonRefundedAmount);
    transaction.setBooking(booking);

    return transactionDAO.save(transaction);
  }

  @Override
  public Transaction addBookingAccepted(Booking booking) {
    /**
     * - amount, description
     */
    Transaction transaction = new Transaction();
    transaction.setType(TransactionType.BOOKING_ACCEPTED);
    transaction.setDescription("Groomer has accepted booking request");

    transaction.setBooking(booking);

    return transactionDAO.save(transaction);
  }

  @Override
  public Transaction addBookingRejected(Booking booking) {
    /**
     * - amount, description
     */
    Transaction transaction = new Transaction();
    transaction.setType(TransactionType.BOOKING_REJECTED);
    transaction.setDescription("Groomer has reject booking request");

    transaction.setBooking(booking);

    return transactionDAO.save(transaction);
  }
}
