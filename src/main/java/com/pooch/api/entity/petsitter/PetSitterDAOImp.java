package com.pooch.api.entity.petsitter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PetSitterDAOImp implements PetSitterDAO {

    @Autowired
    private PetSitterRepository petSitterRepository;

    @Autowired
    private JdbcTemplate        jdbcTemplate;

    @Override
    public PetSitter save(PetSitter petSitter) {
        return petSitterRepository.saveAndFlush(petSitter);
    }

    @Override
    public Optional<PetSitter> getById(long id) {
        return petSitterRepository.findById(id);
    }

    @Override
    public Optional<PetSitter> getByUuid(String uuid) {
        return petSitterRepository.findByUuid(uuid);
    }
}
