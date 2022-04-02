package com.pooch.api.entity.pooch.care;

import com.pooch.api.dto.PoochCareCreateDTO;

public interface PoochCareValidatorService {

    void validateBook(PoochCareCreateDTO petCareCreateDTO);

}
