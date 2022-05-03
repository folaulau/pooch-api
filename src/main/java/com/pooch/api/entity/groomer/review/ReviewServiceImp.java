package com.pooch.api.entity.groomer.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.ReviewCreateDTO;
import com.pooch.api.dto.ReviewDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.groomer.GroomerEvent;
import com.pooch.api.entity.groomer.GroomerUpdateEvent;
import com.pooch.api.entity.parent.Parent;

@Service
@Slf4j
public class ReviewServiceImp implements ReviewService {

    @Autowired
    private ReviewDAO                 reviewDAO;

    @Autowired
    private GroomerDAO                groomerDAO;

    @Autowired
    private EntityDTOMapper           entityDTOMapper;

    @Autowired
    private ReviewValidatorService    reviewValidatorService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ReviewDTO add(ReviewCreateDTO reviewCreateDTO) {
        Pair<Groomer, Parent> pair = reviewValidatorService.validateAddReview(reviewCreateDTO);

        Groomer groomer = pair.getFirst();
        Parent parent = pair.getSecond();

        Review review = entityDTOMapper.mapReviewCreateDTOToReview(reviewCreateDTO);
        review.setGroomerId(groomer.getId());

        if (parent != null && parent.getId() != null) {
            review.setParentId(parent.getId());
        }

        review = reviewDAO.save(review);
        
        groomerDAO.updateRating(groomer.getId());

        applicationEventPublisher.publishEvent(new GroomerUpdateEvent(new GroomerEvent(groomer.getId())));

        return entityDTOMapper.mapReviewToReviewDTO(review);
    }

}
