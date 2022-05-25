package com.pooch.api.entity.pooch;

import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.entity.parent.Parent;

public interface PoochValidatorService {

    void validateCreateUpdatePooch(Parent parent, PoochCreateUpdateDTO poochCreateUpdateDTO);
}
