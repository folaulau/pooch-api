package com.pooch.api.entity.groomer;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.elastic.groomer.GroomerESDAO;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;
import com.pooch.api.entity.groomer.review.ReviewDAO;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GroomerEventHandlerImp implements GroomerEventHandler {

    @Autowired
    private GroomerRepository     groomerRepository;

    @Autowired
    private CareServiceRepository careServiceRepository;

    @Autowired
    private EntityDTOMapper       entityDTOMapper;

    @Autowired
    private ReviewDAO             reviewDAO;

    @Autowired
    private GroomerESDAO          groomerESDAO;

    @EventListener
    @Async
    @Override
    public void refresh(GroomerUpdateEvent groomerUpdateEvent) {
        log.info("\n\n\ngroomerUpdateEvent={}", ObjectUtils.toJson(groomerUpdateEvent));

        GroomerEvent groomerEvent = (GroomerEvent) groomerUpdateEvent.getSource();
        log.info("\n\n\ngroomerEvent={}", ObjectUtils.toJson(groomerEvent));

        Groomer groomer = groomerRepository.getById(groomerEvent.getId());

        if (groomer == null) {
            log.warn("groomer is null");
            return;
        }

        groomer.setRating(reviewDAO.getRatingByGroomerId(groomer.getId()));

        GroomerES groomerES = entityDTOMapper.mapGroomerEntityToGroomerES(groomer);
        groomerES.populateGeoPoints();
        try {
            Optional<Set<CareService>> optCareServices = careServiceRepository.findByGroomerId(groomerES.getId());
            if (optCareServices.isPresent()) {
                groomerES.setCareServices(entityDTOMapper.mapCareServicesToCareServiceESs(optCareServices.get()));
            }
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
        }

        groomerESDAO.save(groomerES);
    }

}
