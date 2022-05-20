package com.pooch.api.library.stripe;

public interface StripeMetadataService {

    /**
     * env: local, dev, github, qa, prod
     */
    String env                           = "environment";

    /**
     * PaymentIntent
     */

    /**
     * groomer uuid of the paymentintent. groomer who's receiving the funds from the paymentintent
     */
    String PAYMENTINTENT_GROOMER_UUID    = "groomer_uuid";

    String PAYMENT_PURPOSE    = "payment_purpose";

    String PAYMENT_PURPOSE_BOOKING_INITIAL_PAYMENT    = "booking_initial_payment";

    // pooch parent uuid for paymentIntent
    String PAYMENTINTENT_PARENT_UUID     = "parent_uuid";

    String PAYMENTINTENT_BOOKING_DETAILS = "booking_details";

}
