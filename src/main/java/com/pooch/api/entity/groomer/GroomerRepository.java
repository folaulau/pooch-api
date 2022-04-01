package com.pooch.api.entity.groomer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroomerRepository extends JpaRepository<Groomer, Long> {

    Optional<Groomer> findByUuid(String uuid);
    
    Optional<Groomer> findByEmail(String email);
}
