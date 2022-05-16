package com.pooch.api.entity.paymentmethod;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PaymentMethodDAOImp implements PaymentMethodDAO {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        return paymentMethodRepository.saveAndFlush(paymentMethod);
    }

    @Override
    public Optional<PaymentMethod> getById(Long id) {
        // TODO Auto-generated method stub
        return paymentMethodRepository.findById(id);
    }

    @Override
    public Optional<PaymentMethod> getByUuid(String uid) {
        // TODO Auto-generated method stub
        return paymentMethodRepository.findByUuid(uid);
    }

    @Override
    public List<PaymentMethod> findByParentId(Long parentId) {
        // TODO Auto-generated method stub
        return paymentMethodRepository.findByParentId(parentId);
    }

    @Override
    public List<PaymentMethod> findByParentUuid(String parentUuid) {
        // TODO Auto-generated method stub
        return paymentMethodRepository.findByParentUuid(parentUuid);
    }

    @Override
    public boolean exist(Long parentId, String stripeId) {
        return paymentMethodRepository.findByParentIdAndStripeId(parentId, stripeId).isPresent();
    }

    @Override
    public Optional<PaymentMethod> getByParentIdAndStripeId(Long parentId, String stripeId) {
        return paymentMethodRepository.findByParentIdAndStripeId(parentId, stripeId);
    }
}
