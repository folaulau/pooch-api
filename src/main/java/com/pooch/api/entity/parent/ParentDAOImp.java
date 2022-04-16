package com.pooch.api.entity.parent;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pooch.api.entity.groomer.Groomer;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ParentDAOImp implements ParentDAO {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private JdbcTemplate     jdbcTemplate;

    @Override
    public Parent save(Parent petParent) {
        return parentRepository.saveAndFlush(petParent);
    }

    @Override
    public Optional<Parent> getByUuid(String uuid) {
        return parentRepository.findByUuid(uuid.trim());
    }

    @Override
    public Optional<Parent> getByEmail(String email) {
        // TODO Auto-generated method stub
        return parentRepository.findByEmail(email);
    }

    @Override
    public boolean existEmail(String email) {
        return Optional.ofNullable(parentRepository.getIdByEmail(email)).isPresent();
    }

}
