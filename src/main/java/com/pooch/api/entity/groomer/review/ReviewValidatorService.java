package com.pooch.api.entity.groomer.review;

import org.springframework.data.util.Pair;

import com.pooch.api.dto.ReviewCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;

public interface ReviewValidatorService {

    Pair<Groomer, Parent> validateAddReview(ReviewCreateDTO review);
}
