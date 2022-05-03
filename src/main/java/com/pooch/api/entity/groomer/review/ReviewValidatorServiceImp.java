package com.pooch.api.entity.groomer.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ReviewCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReviewValidatorServiceImp implements ReviewValidatorService {

    @Autowired
    private ParentDAO  parentDAO;

    @Autowired
    private GroomerDAO groomerDAO;

    @Override
    public Pair<Groomer, Parent> validateAddReview(ReviewCreateDTO review) {

        double rating = review.getRating();

        if (rating < 0 || rating > 5) {
            throw new ApiException("rating is invalid","valid values 1-5");
        }

        String groomerUuid = review.getGroomerUuid();

        Groomer groomer = groomerDAO.getByUuid(groomerUuid).orElseThrow(() -> new ApiException("Groomer not found", "groomer not found for uuid=" + groomerUuid));

        String parentUuid = review.getParentUuid();

        Parent parent = new Parent();//parentDAO.getByUuid(parentUuid).orElseThrow(() -> new ApiException("Parent not found", "parent not found for uuid=" + parentUuid));

        return Pair.of(groomer, parent);
    }

}
