package com.pooch.api.entity.pooch;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PoochDAOImp implements PoochDAO {

    @Autowired
    private PoochRepository poochRepository;

    @Autowired
    private JdbcTemplate  jdbcTemplate;

    @Override
    public Pooch save(Pooch pet) {
        return poochRepository.saveAndFlush(pet);
    }

    @Override
    public Optional<Pooch> getByUuid(String uuid) {
        return poochRepository.findByUuid(uuid);
    }

}
