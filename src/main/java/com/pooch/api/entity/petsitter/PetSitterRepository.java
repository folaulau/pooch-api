package com.pooch.api.entity.petsitter;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

    Optional<PetSitter> findByUuid(String uuid);
}
