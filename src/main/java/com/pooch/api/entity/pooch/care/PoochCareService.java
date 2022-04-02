package com.pooch.api.entity.pooch.care;

import com.pooch.api.dto.PoochCareCreateDTO;
import com.pooch.api.dto.PoochCareDTO;

public interface PoochCareService {

    PoochCareDTO book(PoochCareCreateDTO petCareCreateDTO);
}
