package com.pooch.api.elastic;

import org.springframework.scheduling.annotation.Async;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.entity.groomer.Groomer;

public interface DataLoadService {

    ApiDefaultResponseDTO loadGroomers();

    @Async
    void reloadGroomer(Groomer groomer);

    @Async
    void reloadGroomer(Groomer groomer, double rating);
}
