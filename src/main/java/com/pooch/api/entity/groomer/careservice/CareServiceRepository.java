package com.pooch.api.entity.groomer.careservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CareServiceRepository extends JpaRepository<CareService, Long> {

    Set<CareService> findByGroomerId(Long groomerId);
}
