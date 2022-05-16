package com.pooch.api.entity.paymentmethod;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodDAO {

    PaymentMethod save(PaymentMethod paymentMethod);

    Optional<PaymentMethod> getById(Long id);

    Optional<PaymentMethod> getByUuid(String uuid);

    List<PaymentMethod> findByParentId(Long parentId);

    List<PaymentMethod> findByParentUuid(String parentUuid);

    boolean exist(Long parentId, String stripeId);

    Optional<PaymentMethod> getByParentIdAndStripeId(Long id, String paymentMethod);
}
