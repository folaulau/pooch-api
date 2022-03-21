package com.pooch.api.entity.petcare;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PetCareCreateDTO;
import com.pooch.api.dto.PetCareDTO;
import com.pooch.api.dto.PetCreateDTO;
import com.pooch.api.dto.PetDTO;
import com.pooch.api.dto.PetParentUpdateDTO;
import com.pooch.api.entity.pet.Pet;
import com.pooch.api.entity.pet.PetDAO;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.entity.petparent.PetParentDAO;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetCareServiceImp implements PetCareService {

    @Autowired
    private PetCareDAO              petCareDAO;

    @Autowired
    private EntityDTOMapper         entityDTOMapper;

    @Autowired
    private PetSitterDAO            petSitterDAO;

    @Autowired
    private PetParentDAO            petParentDAO;

    @Autowired
    private PetDAO                  petDAO;

    @Autowired
    private PetCareValidatorService petCareValidatorService;

    @Override
    public PetCareDTO book(PetCareCreateDTO petCareCreateDTO) {
        petCareValidatorService.validateBook(petCareCreateDTO);

        PetCareDTO petCareDTO = new PetCareDTO();

        PetParentUpdateDTO petParentUpdateDTO = petCareCreateDTO.getPetParent();

        PetParent petParent = null;

        if (petParentUpdateDTO.getUuid() != null) {
            petParent = petParentDAO.getByUuid(petParentUpdateDTO.getUuid()).get();
            entityDTOMapper.patchPetParentWithPetParentUpdateDTO(petParentUpdateDTO, petParent);
        } else {
            petParent = entityDTOMapper.mapPetParentUpdateDTOToPetParent(petParentUpdateDTO);
        }

        petParent = petParentDAO.save(petParent);

        Set<PetCreateDTO> petCreateDTOs = petCareCreateDTO.getPets();

        if (petCreateDTOs != null) {

            Set<PetDTO> petDTOs = new HashSet<>();
            for (PetCreateDTO petCreateDTO : petCreateDTOs) {
                String uuid = petCreateDTO.getUuid();

                Pet pet = null;
                if (uuid == null) {
                    pet = entityDTOMapper.mapPetCreateDTOToPet(petCreateDTO);
                    pet.setPetParent(petParent);
                    pet = petDAO.save(pet);
                } else {
                    pet = petDAO.getByUuid(uuid).get();
                }

                PetDTO petDTO = entityDTOMapper.mapPetToPetDTO(pet);
                petDTOs.add(petDTO);
            }

            petCareDTO.setPets(petDTOs);
        }

        String petSitterUuid = petCareCreateDTO.getPetSitterUuid();

        if (petSitterUuid != null) {

            PetSitter petSitter = petSitterDAO.getByUuid(petSitterUuid).get();
            petCareDTO.setPetSitter(entityDTOMapper.mapPetSitterToPetSitterDTO(petSitter));

        }

        petCareDTO.setPetParent(entityDTOMapper.mapPetParentToPetParentDTO(petParent));

        return petCareDTO;
    }

}
