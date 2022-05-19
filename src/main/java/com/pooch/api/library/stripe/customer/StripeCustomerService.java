package com.pooch.api.library.stripe.customer;

import com.pooch.api.entity.parent.Parent;
import com.stripe.model.Customer;

public interface StripeCustomerService {

    com.stripe.model.Customer updateParentDetails(Parent parent);

    Customer createPlaceHolderCustomer();

    Customer getById(String id);
}
