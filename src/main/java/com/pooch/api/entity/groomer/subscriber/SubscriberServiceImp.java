package com.pooch.api.entity.groomer.subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.SubscriberCreateDTO;
import com.pooch.api.entity.notification.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubscriberServiceImp implements SubscriberService {

    @Autowired
    private SubscriberDAO       subscriberDAO;

    @Autowired
    private EntityDTOMapper     entityDTOMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Subscriber add(SubscriberCreateDTO subscribeCreateDTO) {

        Subscriber subscriber = entityDTOMapper.mapSubscriberCreateDTOToSubscriber(subscribeCreateDTO);

        try {
            subscriber = subscriberDAO.save(subscriber);

            notificationService.sendGroomerSubcribingNtc(subscriber);
            
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
        }

        return subscriber;
    }

}
