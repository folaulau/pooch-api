package com.pooch.api.library.stripe.paymentintent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.PaymentIntentCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StripePaymentIntentValidatorServiceImp implements StripePaymentIntentValidatorService {

    @Autowired
    private GroomerDAO groomerDAO;

    @Override
    public Groomer validateProcessNewPaymentIntent(PaymentIntentCreateDTO paymentIntentCreateDTO) {

        String groomerUuid = paymentIntentCreateDTO.getGroomerUuid();

        if (groomerUuid == null || groomerUuid.trim().isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "groomerUuid is required");
        }

        Double amount = paymentIntentCreateDTO.getAmount();
        //
        // if (amount == null) {
        // throw new ApiException("Amount is required", "amount is required");
        // }
        //

        if (amount != null && amount < 0) {
            throw new ApiException("Amount is invalid", "amount=" + amount);
        }
        
        if (amount != null && amount > 100000) {
            throw new ApiException("Amount is invalid", "amount=" + amount);
        }

        Groomer groomer = groomerDAO.getByUuid(groomerUuid).orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "groomer not found for uuid=" + groomerUuid));

        if (!groomer.isActive()) {
            throw new ApiException("Groomer is not ready to take in business", "status=" + groomer.getStatus());
        }

        return groomer;
    }
}
