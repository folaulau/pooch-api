package com.pooch.api.entity.parent;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ParentDAOImp implements ParentDAO {

    @Autowired
    private ParentRepository petParentRepository;

    @Autowired
    private JdbcTemplate        jdbcTemplate;

    @Override
    public Parent save(Parent petParent) {
        return petParentRepository.saveAndFlush(petParent);
    }

    @Override
    public Optional<Parent> getByUuid(String uuid) {
        return petParentRepository.findByUuid(uuid.trim());
    }

}
