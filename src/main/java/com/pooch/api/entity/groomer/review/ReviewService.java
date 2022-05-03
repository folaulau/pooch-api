package com.pooch.api.entity.groomer.review;

import com.pooch.api.dto.ReviewCreateDTO;
import com.pooch.api.dto.ReviewDTO;

public interface ReviewService {

    ReviewDTO add(ReviewCreateDTO review);

}
