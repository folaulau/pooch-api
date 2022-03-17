package com.pooch.api.entity.pet;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PetDAOImp implements PetDAO {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private JdbcTemplate  jdbcTemplate;

    @Override
    public Pet save(Pet pet) {
        return petRepository.saveAndFlush(pet);
    }

    @Override
    public Optional<Pet> getByUuid(String uuid) {
        return petRepository.findByUuid(uuid);
    }

}
