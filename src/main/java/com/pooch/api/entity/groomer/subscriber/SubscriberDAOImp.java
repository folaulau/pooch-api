package com.pooch.api.entity.groomer.subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SubscriberDAOImp implements SubscriberDAO {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public Subscriber save(Subscriber subscriber) {
        return subscriberRepository.saveAndFlush(subscriber);
    }
}
