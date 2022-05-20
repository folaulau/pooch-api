package com.pooch.api.library.stripe.customer;

import com.pooch.api.entity.parent.Parent;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;

public interface StripeCustomerService {

    com.stripe.model.Customer updateParentDetails(Parent parent);
    
    com.stripe.model.Customer createParentDetails(Parent parent);

//    Customer createPlaceHolderCustomer();

    Customer getById(String id);

    Customer addPaymentMethod(Parent parent, PaymentMethod stripePaymentMethod);
}
