package com.pooch.api.library.stripe.paymentintent;

import java.util.Arrays;

/**
 * https://stripe.com/docs/api/payment_intents/object#payment_intent_object-status<br>
 * https://stripe.com/docs/payments/intents#intent-statuses
 */
public interface StripePaymentIntentStatus {

    String requiresPaymentMethod = "requires_payment_method";
    String requiresConfirmation  = "requires_confirmation";
    String requiresAction        = "requires_action";
    String processing            = "processing";
    String requiresCapture       = "requiresCapture";
    String canceled              = "canceled";
    String succeeded             = "succeeded";

    public static boolean doesRequireUserAction(String status) {
        if(status==null) {
            return false;
        }
        return Arrays.asList(requiresPaymentMethod, requiresConfirmation, requiresAction).contains(status);
    }

}
