package com.pooch.api.entity.parent.paymentmethod;

import org.springframework.data.jpa.repository.JpaRepository;
import java.lang.String;
import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    Optional<PaymentMethod> findByUuid(String uid);

    List<PaymentMethod> findByParentId(Long parentId);

    List<PaymentMethod> findByParentUuid(String parentUuid);

    Optional<PaymentMethod> findByParentIdAndStripeId(Long parentId, String stripeId);
}
