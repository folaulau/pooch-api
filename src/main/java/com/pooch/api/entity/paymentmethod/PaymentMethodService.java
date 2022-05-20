package com.pooch.api.entity.paymentmethod;

import java.util.List;
import com.pooch.api.dto.PaymentMethodCreateDTO;
import com.pooch.api.dto.PaymentMethodDTO;
import com.pooch.api.entity.parent.Parent;

public interface PaymentMethodService {

  PaymentMethod create(PaymentMethod paymentMethod);

  PaymentMethod update(PaymentMethod paymentMethod);

  // PaymentMethod getById(Long id);
  //
  // PaymentMethod getByUuid(String uid);

  List<PaymentMethod> getByParentId(Long parentId);

  List<PaymentMethod> getByParentUuid(String parentUuid);


  // List<PaymentMethod> remove(Long accountId, PaymentMethodDeleteDTO paymentMethodDeleteDTO);

  List<PaymentMethod> update(Long parentUuid, String paymentMethodUuid,
      PaymentMethod newPaymentMethod);

  // PaymentMethod add(Parent parent, com.stripe.model.PaymentMethod stripePaymentMethod);

  PaymentMethod add(Parent parent, com.stripe.model.PaymentMethod stripePaymentMethod);

  PaymentMethod mapStripePaymentMethodToPaymentMethod(
      com.stripe.model.PaymentMethod stripePaymentMethod);

  PaymentMethodDTO add(String parentUuid, PaymentMethodCreateDTO paymentMethodCreateDTO);

  // PaymentMethod add(Parent parent, String stripePaymentMethodId);
}
