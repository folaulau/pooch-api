package com.pooch.api.entity.booking;

public enum BookingStripeStatus {

    FUNDS_IN_POOCH_ACCOUNT,
    FUNDS_IN_GROOMER_ACCOUNT,
    CANCELLED,
    CANCELLED_AND_REFUNDED,
    REFUNDED;
}
