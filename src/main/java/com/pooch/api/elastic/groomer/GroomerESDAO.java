package com.pooch.api.elastic.groomer;

import com.pooch.api.dto.CustomPage;
import com.pooch.api.dto.GroomerSearchParamsDTO;

import java.util.Set;

import org.springframework.scheduling.annotation.Async;

import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.careservice.CareService;

public interface GroomerESDAO {

    @Async
    void save(GroomerES groomerES);

    CustomPage<GroomerES> search(GroomerSearchParamsDTO params);
}
