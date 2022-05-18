package com.pooch.api.entity.pooch;

import java.util.List;
import java.util.Set;

import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.dto.PoochDTO;
import com.pooch.api.entity.parent.Parent;

public interface PoochService {

    List<PoochDTO> updatePooches(Parent petParent, Set<PoochCreateUpdateDTO> petCreateDTOs);
}
