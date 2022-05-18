package com.pooch.api.entity.pooch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.dto.PoochDTO;
import com.pooch.api.entity.parent.Parent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PoochServiceImp implements PoochService {

    @Autowired
    private PoochDAO        poochDAO;

    @Autowired
    private EntityDTOMapper entityDTOMapper;

    @Override
    public List<PoochDTO> updatePooches(Parent parent, Set<PoochCreateUpdateDTO> poochCreateUpdateDTOs) {
        
        if(poochCreateUpdateDTOs==null || poochCreateUpdateDTOs.size()<=0) {
            return new ArrayList<PoochDTO>();
        }

        List<PoochDTO> poochDTOS = poochCreateUpdateDTOs.stream().map(petCreateDTO -> {

            String uuid = petCreateDTO.getUuid();

            Optional<Pooch> optPet = poochDAO.getByUuid(uuid);

            Pooch pooch = null;

            if (optPet.isPresent()) {
                pooch = optPet.get();
                entityDTOMapper.patchPet(petCreateDTO, pooch);
            } else {
                pooch = entityDTOMapper.mapPoochCreateDTOToPooch(petCreateDTO);
            }
            
            pooch.setParent(parent);

            pooch = poochDAO.save(pooch);

            PoochDTO petDTO = entityDTOMapper.mapPoochToPoochDTO(pooch);
            return petDTO;

        }).collect(Collectors.toList());

        return poochDTOS;
    }
}
