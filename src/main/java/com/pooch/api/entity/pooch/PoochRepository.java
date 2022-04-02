package com.pooch.api.entity.pooch;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PoochRepository extends JpaRepository<Pooch, Long> {

    Optional<Pooch> findByUuid(String uuid);
}
