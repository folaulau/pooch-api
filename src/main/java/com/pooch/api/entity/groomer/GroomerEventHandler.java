package com.pooch.api.entity.groomer;

import org.springframework.scheduling.annotation.Async;

public interface GroomerEventHandler {

    @Async
    public void refresh(GroomerUpdateEvent groomerUpdateEvent);
}
