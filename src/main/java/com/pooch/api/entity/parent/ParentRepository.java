package com.pooch.api.entity.parent;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByUuid(String uuid);
    
    Optional<Parent> findByEmail(String email);
}
