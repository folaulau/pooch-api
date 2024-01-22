package com.pooch.api.entity.groomer.subscriber;
import com.pooch.api.dto.SubscriberCreateDTO;

public interface SubscriberService {

    Subscriber add(SubscriberCreateDTO subscribeCreateDTO);
}
