package com.pooch.api.entity.groomer;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GroomerEventHandlerImp implements GroomerEventHandler {

    @EventListener
    @Async
    @Override
    public void refresh(GroomerUpdateEvent groomerUpdateEvent) {
        log.info("\n\n\ngroomerUpdateEvent={}", ObjectUtils.toJson(groomerUpdateEvent));
    }

}
