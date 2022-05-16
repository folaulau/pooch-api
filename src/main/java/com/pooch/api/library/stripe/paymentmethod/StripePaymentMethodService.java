package com.pooch.api.library.stripe.paymentmethod;

public interface StripePaymentMethodService {

    com.stripe.model.PaymentMethod getById(String id);
}
