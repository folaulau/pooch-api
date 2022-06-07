package com.pooch.api.entity.booking.transaction;

import javax.persistence.Column;

public enum TransactionType {

    /**
     * Description<br>
     * - Payment made at booking<br>
     * Fields<br>
     * - bookingCost, bookingFee, stripeFee, totalChargeNowAmount, totalChargeAtDropOffAmount, description
     */
    BOOKING_INITIAL_PAYMENT,

    /**
     * Description<br>
     * - Payment made after initial payment. It could be a charge for additional requested services outside of the
     * original request<br>
     * Fields<br>
     * - amount, description
     */
    BOOKING_PAYMENT,

    /**
     * Description<br>
     * - tip Groomers for their services<br>
     * Fields<br>
     * - amount, description
     */
    BOOKING_TIP_PAYMENT,

    /**
     * Description<br>
     * - Groomer accepts booking<br>
     * Fields<br>
     * - amount, description
     */
    BOOKING_ACCEPTED,

    BOOKING_REJECTED,

    /**
     * Description<br>
     * - Groomer accepts booking<br>
     * Fields<br>
     * - cancelUserType, cancelUserId, refundedAmount, nonRefundedAmount, description
     */
    BOOKING_CANCELLATION,


    /**
     * Description<br>
     * - Groomer refunds to Parent<br>
     * Fields<br>
     * - refundedAmount, nonRefundedAmount, description
     */
    BOOKING_REFUND
}
