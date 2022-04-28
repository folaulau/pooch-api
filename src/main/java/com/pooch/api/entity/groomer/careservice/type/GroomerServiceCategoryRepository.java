package com.pooch.api.entity.groomer.careservice.type;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroomerServiceCategoryRepository extends JpaRepository<GroomerServiceCategory, Long>{

    Optional<GroomerServiceCategory> findByName(String name);
}
