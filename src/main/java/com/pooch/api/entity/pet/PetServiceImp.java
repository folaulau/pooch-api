package com.pooch.api.entity.pet;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PetCreateDTO;
import com.pooch.api.dto.PetDTO;
import com.pooch.api.dto.VaccineCreateDTO;
import com.pooch.api.entity.parent.Parent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetServiceImp implements PetService {

    @Autowired
    private PetDAO            petDAO;

    @Autowired
    private EntityDTOMapper   entityDTOMapper;

    @Override
    public List<PetDTO> add(Parent petParent, Set<PetCreateDTO> petCreateDTOs) {

        List<PetDTO> petDTOs = petCreateDTOs.stream().map(petCreateDTO -> {

            String uuid = petCreateDTO.getUuid();

            Optional<Pet> optPet = petDAO.getByUuid(uuid);

            Pet pet = null;

            if (optPet.isPresent()) {
                pet = optPet.get();
                entityDTOMapper.patchPet(petCreateDTO, pet);
            } else {
                pet = entityDTOMapper.mapPetCreateDTOToPet(petCreateDTO);
                pet.setParent(petParent);
            }
            
            pet = petDAO.save(pet);
            
            PetDTO petDTO = entityDTOMapper.mapPetToPetDTO(pet);
            return petDTO;

        }).toList();

        return petDTOs;
    }
}
