package com.pooch.api.entity.booking;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.BookingDTO;
import com.pooch.api.dto.PoochCreateDTO;
import com.pooch.api.dto.PoochDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImp implements BookingService {

    @Autowired
    private BookingDAO              poochCareDAO;

    @Autowired
    private EntityDTOMapper         entityDTOMapper;

    @Autowired
    private GroomerDAO              groomerDAO;

    @Autowired
    private ParentDAO               parentDAO;

    @Autowired
    private PoochDAO                poochDAO;

    @Autowired
    private BookingValidatorService petCareValidatorService;

    @Override
    public BookingDTO book(BookingCreateDTO petCareCreateDTO) {
        petCareValidatorService.validateBook(petCareCreateDTO);

        BookingDTO petCareDTO = new BookingDTO();

        ParentCreateUpdateDTO parentCreateUpdateDTO = petCareCreateDTO.getParent();

        Parent parent = null;

        if (parentCreateUpdateDTO.getUuid() != null) {
            parent = parentDAO.getByUuid(parentCreateUpdateDTO.getUuid()).get();
            entityDTOMapper.patchParentWithNewParentUpdateDTO(parentCreateUpdateDTO, parent);
        } else {
            parent = entityDTOMapper.mapNewUpdateDTOToParent(parentCreateUpdateDTO);
        }

        parent = parentDAO.save(parent);

        Set<PoochCreateDTO> petCreateDTOs = petCareCreateDTO.getPooches();
        if (petCreateDTOs != null) {

            Set<PoochDTO> petDTOs = new HashSet<>();
            for (PoochCreateDTO petCreateDTO : petCreateDTOs) {
                String uuid = petCreateDTO.getUuid();

                Pooch pooch = null;
                if (uuid == null) {
                    pooch = entityDTOMapper.mapPoochCreateDTOToPooch(petCreateDTO);
                    pooch.setParent(parent);
                    pooch = poochDAO.save(pooch);
                } else {
                    pooch = poochDAO.getByUuid(uuid).get();
                }

                PoochDTO petDTO = entityDTOMapper.mapPoochToPoochDTO(pooch);
                petDTOs.add(petDTO);
            }

            petCareDTO.setPooches(petDTOs);
        }

        String groomerUuid = petCareCreateDTO.getGroomerUuid();

        if (groomerUuid != null) {

            Groomer groomer = groomerDAO.getByUuid(groomerUuid).get();
            petCareDTO.setGroomer(entityDTOMapper.mapGroomerToGroomerDTO(groomer));

        }

        petCareDTO.setParent(entityDTOMapper.mapPetParentToPetParentDTO(parent));

        return petCareDTO;
    }

}
