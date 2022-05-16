package com.pooch.api.library.stripe;

public interface StripeMetadataService {

    /**
     * env: local, dev, github, qa, prod
     */
    String env                        = "environment";

    /**
     * PaymentIntent
     */

    /**
     * groomer uuid of the paymentintent. groomer who's receiving the funds from the paymentintent
     */
    String PAYMENTINTENT_GROOMER_UUID = "receiver_groomer_uuid";
    String PAYMENTINTENT_BOOKING_COST = "groomer_booking_cost";

}
