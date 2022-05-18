package com.pooch.api.entity.pooch;

import com.pooch.api.dto.PoochCreateUpdateDTO;

public interface PoochValidatorService {

    void validateCreateUpdatePooch(PoochCreateUpdateDTO poochCreateUpdateDTO);
}
