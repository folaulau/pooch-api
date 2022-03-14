package com.pooch.api.entity.petparent;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PetParentDAOImp implements PetParentDAO {

    @Autowired
    private PetParentRepository petParentRepository;

    @Override
    public PetParent save(PetParent petParent) {
        return petParentRepository.saveAndFlush(petParent);
    }

    @Override
    public Optional<PetParent> getByUuid(String uuid) {
        return petParentRepository.findByUuid(uuid);
    }

}
