package com.pooch.api.library.stripe;

public interface StripeMetadataService {

    /**
     * env: local, dev, github, qa, prod
     */
    String env                                     = "environment";

    /**
     * PaymentIntent
     */

    /**
     * groomer uuid of the paymentintent. groomer who's receiving the funds from the paymentintent
     */
    String PAYMENTINTENT_GROOMER_UUID              = "groomer_uuid";
    String PAYMENTINTENT_BOOKING_COST              = "booking_cost";
    String PAYMENTINTENT_BOOKING_TOTAL_AT_CHECKOUT = "total_at_checkout";
    String PAYMENTINTENT_BOOKING_TOTAL_AT_DROPOFF  = "total_at_dropoff";
    String PAYMENTINTENT_BOOKING_STRIPE_FEE        = "stripe_fee";

}
