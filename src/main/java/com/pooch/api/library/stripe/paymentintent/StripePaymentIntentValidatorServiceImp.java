package com.pooch.api.library.stripe.paymentintent;

import com.pooch.api.entity.paymentmethod.PaymentMethod;
import com.pooch.api.entity.paymentmethod.PaymentMethodDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.PaymentIntentParentCreateDTO;
import com.pooch.api.dto.PaymentIntentQuestCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StripePaymentIntentValidatorServiceImp implements StripePaymentIntentValidatorService {

    @Autowired
    private GroomerDAO groomerDAO;

    @Autowired
    private ParentDAO parentDAO;

    @Autowired
    private PaymentMethodDAO paymentMethodDAO;

    @Override
    public Groomer validateCreateQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentCreateDTO) {

        String groomerUuid = paymentIntentCreateDTO.getGroomerUuid();

        if (groomerUuid == null || groomerUuid.trim().isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "groomerUuid is required");
        }

        Double amount = paymentIntentCreateDTO.getAmount();
        //
        // if (amount == null) {
        // throw new ApiException("Amount is required", "amount is required");
        // }

        if (amount != null && amount < 0) {
            throw new ApiException("Amount is invalid", "amount=" + amount);
        }

        if (amount != null && amount > 100000) {
            throw new ApiException("Amount is invalid", "amount=" + amount);
        }

        Groomer groomer = groomerDAO.getByUuid(groomerUuid).orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "groomer not found for uuid=" + groomerUuid));

        // if (!groomer.isActive()) {
        // throw new ApiException("Groomer is not ready to take in business", "status=" + groomer.getStatus());
        // }

        return groomer;
    }

    @Override
    public Groomer validateUpdateQuestPaymentIntent(PaymentIntentQuestCreateDTO paymentIntentQuestUpdateDTO) {
        String groomerUuid = paymentIntentQuestUpdateDTO.getGroomerUuid();

        if (groomerUuid == null || groomerUuid.trim().isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "groomerUuid is required");
        }

        String paymentIntentId = paymentIntentQuestUpdateDTO.getPaymentIntentId();

        if (paymentIntentId == null || paymentIntentId.trim().isEmpty() || !paymentIntentId.substring(0, 3).equalsIgnoreCase("pi_")) {
            throw new ApiException(ApiError.DEFAULT_MSG, "paymentIntentId is invalid", "paymentIntentId=" + paymentIntentId, "paymentIntentId starts with pi_");
        }

        Double amount = paymentIntentQuestUpdateDTO.getAmount();
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

        // if (!groomer.isActive()) {
        // throw new ApiException("Groomer is not ready to take in business", "status=" + groomer.getStatus());
        // }

        return groomer;
    }

    @Override
    public Pair<Groomer, Parent> validateCreateParentPaymentIntent(PaymentIntentParentCreateDTO paymentIntentParentDTO) {
        String groomerUuid = paymentIntentParentDTO.getGroomerUuid();

        if (groomerUuid == null || groomerUuid.trim().isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "groomerUuid is required");
        }

        Double amount = paymentIntentParentDTO.getAmount();
        //
        // if (amount == null) {
        // throw new ApiException("Amount is required", "amount is required");
        // }

        if (amount != null && amount < 0) {
            throw new ApiException("Amount is invalid", "amount=" + amount);
        }

        if (amount != null && amount > 100000) {
            throw new ApiException("Amount is invalid", "amount=" + amount);
        }

        String parentUuid = paymentIntentParentDTO.getParentUuid();

        if (parentUuid == null || parentUuid.trim().isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "parentUuid is required");
        }

        Groomer groomer = groomerDAO.getByUuid(groomerUuid).orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "groomer not found for uuid=" + groomerUuid));

        // if (!groomer.isActive()) {
        // throw new ApiException("Groomer is not ready to take in business", "status=" + groomer.getStatus());
        // }

        Parent parent = parentDAO.getByUuid(parentUuid).orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "parent not found for uuid=" + parentUuid));

        String paymentMethodUuid = paymentIntentParentDTO.getPaymentMethodUuid();

        if (paymentMethodUuid != null || !paymentMethodUuid.trim().isEmpty()) {
            PaymentMethod paymentMethod = paymentMethodDAO.getByUuid(paymentMethodUuid).orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "PaymentMethod not found for uuid=" + paymentMethodUuid));

            if (paymentMethod.getParent() == null || !paymentMethod.getParent().getId().equals(parent.getId())) {
                throw new ApiException(ApiError.DEFAULT_MSG, "PaymentMethod does not belong to parent");
            }
        }


        return Pair.of(groomer, parent);
    }

    @Override
    public Pair<Groomer, Parent> validateUpdateParentPaymentIntent(PaymentIntentParentCreateDTO paymentIntentParentDTO) {
        String groomerUuid = paymentIntentParentDTO.getGroomerUuid();

        if (groomerUuid == null || groomerUuid.trim().isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "groomerUuid is required");
        }

        String paymentIntentId = paymentIntentParentDTO.getPaymentIntentId();

        if (paymentIntentId == null || paymentIntentId.trim().isEmpty() || !paymentIntentId.substring(0, 3).equalsIgnoreCase("pi_")) {
            throw new ApiException(ApiError.DEFAULT_MSG, "paymentIntentId is invalid", "paymentIntentId=" + paymentIntentId, "paymentIntentId starts with pi_");
        }

        Double amount = paymentIntentParentDTO.getAmount();
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

        String parentUuid = paymentIntentParentDTO.getParentUuid();

        if (parentUuid == null || parentUuid.trim().isEmpty()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "parentUuid is required");
        }

        Groomer groomer = groomerDAO.getByUuid(groomerUuid).orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "groomer not found for uuid=" + groomerUuid));

        // if (!groomer.isActive()) {
        // throw new ApiException("Groomer is not ready to take in business", "status=" + groomer.getStatus());
        // }

        Parent parent = parentDAO.getByUuid(parentUuid).orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "parent not found for uuid=" + parentUuid));

        String paymentMethodUuid = paymentIntentParentDTO.getPaymentMethodUuid();

        if (paymentMethodUuid != null || !paymentMethodUuid.trim().isEmpty()) {
            PaymentMethod paymentMethod = paymentMethodDAO.getByUuid(paymentMethodUuid).orElseThrow(() -> new ApiException(ApiError.DEFAULT_MSG, "PaymentMethod not found for uuid=" + paymentMethodUuid));

            if (paymentMethod.getParent() == null || !paymentMethod.getParent().getId().equals(parent.getId())) {
                throw new ApiException(ApiError.DEFAULT_MSG, "PaymentMethod does not belong to parent");
            }
        }

        return Pair.of(groomer, parent);
    }
}
