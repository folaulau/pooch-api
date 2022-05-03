package com.pooch.api.entity.groomer;

import org.springframework.context.ApplicationEvent;

import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroomerUpdateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public GroomerUpdateEvent(GroomerEvent groomerEvent) {
        super(groomerEvent);
        log.debug("GroomerUpdateEvent, groomerEvent={}", ObjectUtils.toJson(groomerEvent));
    }

}
