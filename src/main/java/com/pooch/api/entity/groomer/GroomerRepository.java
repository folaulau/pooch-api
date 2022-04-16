package com.pooch.api.entity.groomer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroomerRepository extends JpaRepository<Groomer, Long> {

    Optional<Groomer> findByUuid(String uuid);

    Optional<Groomer> findByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT id FROM groomer WHERE email = :email ")
    Long getIdByEmail(@Param(value = "email") String email);
}
