package com.pooch.api.library.stripe.account;

import com.pooch.api.entity.groomer.Groomer;
import com.stripe.param.AccountLinkCreateParams;

public interface StripeAccountService {

    com.stripe.model.Account create(Groomer groomer);

    com.stripe.model.Account getById(String id);

    com.stripe.model.AccountLink getByAccountId(String accountId, AccountLinkCreateParams.Type type);
}
