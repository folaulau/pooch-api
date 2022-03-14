package com.pooch.api.entity.petparent;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetParentRepository extends JpaRepository<PetParent, Long> {

    Optional<PetParent> findByUuid(String uuid);
}
