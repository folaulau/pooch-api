package com.pooch.api.entity.groomer.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
class ReviewDAOImp implements ReviewDAO {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JdbcTemplate     jdbcTemplate;

    @Override
    public Review save(Review review) {
        return reviewRepository.saveAndFlush(review);
    }

    @Override
    public double getRatingByGroomerId(long groomerId) {

        StringBuilder query = new StringBuilder();

        query.append("");

        double averageRating = 0;

        try {
            averageRating = jdbcTemplate.queryForObject(query.toString(), Double.class, new Object[]{groomerId});
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
        }

        return averageRating;

    }

}
