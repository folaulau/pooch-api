package com.pooch.api.entity.parent;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByUuid(String uuid);

    Optional<Parent> findByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT id FROM parent WHERE email = :email ")
    Long getIdByEmail(@Param(value = "email") String email);
}
