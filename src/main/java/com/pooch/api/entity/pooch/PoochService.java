package com.pooch.api.entity.pooch;

import java.util.List;
import java.util.Set;

import com.pooch.api.dto.PoochCreateDTO;
import com.pooch.api.dto.PoochDTO;
import com.pooch.api.entity.parent.Parent;

public interface PoochService {

    List<PoochDTO> add(Parent petParent, Set<PoochCreateDTO> petCreateDTOs);
}
