package com.pooch.api.entity.groomer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GroomerAOImp implements GroomerDAO {

    @Autowired
    private GroomerRepository groomerRepository;

    @Autowired
    private JdbcTemplate      jdbcTemplate;

    @Override
    public Groomer save(Groomer petSitter) {
        return groomerRepository.saveAndFlush(petSitter);
    }

    @Override
    public Optional<Groomer> getById(long id) {
        return groomerRepository.findById(id);
    }

    @Override
    public Optional<Groomer> getByUuid(String uuid) {
        return groomerRepository.findByUuid(uuid);
    }
}
