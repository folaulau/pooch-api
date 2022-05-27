package com.pooch.api.entity.notification.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class EmailDAOImp implements EmailDAO {

    @Autowired
    private EmailRepository emailRepository;

    @Override
    public Email save(Email email) {
        return emailRepository.saveAndFlush(email);
    }
}
