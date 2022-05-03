package com.pooch.api.entity.groomer.review;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewDAO {

    Review save(Review review);

	double getRatingByGroomerId(long groomerId);
}
