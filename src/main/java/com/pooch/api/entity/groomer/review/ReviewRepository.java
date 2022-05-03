package com.pooch.api.entity.groomer.review;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface ReviewRepository extends JpaRepository<Review, Long> {

}
