package com.pooch.api.library.stripe.paymentmethod;

public interface StripePaymentMethodService {

    com.stripe.model.PaymentMethod getById(String id);
    
    com.stripe.model.PaymentMethod attachToCustomer(String paymentMethodId, String customerId);
    
    com.stripe.model.PaymentMethod detachFromCustomer(String paymentMethodId);
}
