package com.pooch.api.entity.pet;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByUuid(String uuid);
}
