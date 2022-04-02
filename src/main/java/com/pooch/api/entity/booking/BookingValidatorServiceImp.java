package com.pooch.api.entity.booking;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.PoochCreateDTO;
import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.GroomerUuidDTO;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingValidatorServiceImp implements BookingValidatorService {

    @Autowired
    private GroomerDAO groomerDAO;

    @Autowired
    private ParentDAO  parentDAO;

    @Autowired
    private PoochDAO   poochDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Override
    public void validateBook(BookingCreateDTO petCareCreateDTO) {
        ParentCreateUpdateDTO parentCreateUpdateDTO = petCareCreateDTO.getParent();

        if (parentCreateUpdateDTO == null) {
            throw new ApiException(ApiError.DEFAULT_MSG, "petParent is required");
        }

        String parentUuid = parentCreateUpdateDTO.getUuid();

        Optional<Parent> optParent = parentDAO.getByUuid(parentUuid);

        if (!optParent.isPresent()) {
            throw new ApiException(ApiError.DEFAULT_MSG, "parent not found for uuid=" + parentUuid);
        }

        Parent parent = optParent.get();

        // String petSitterUuid = petCareCreateDTO.getPetSitterUuid();
        //
        // if (null == petSitterUuid || petSitterUuid.isEmpty()) {
        // throw new ApiException(ApiError.DEFAULT_MSG, "petSitter not found for uuid=" + petSitterUuid);
        // }
        //
        // Optional<PetSitter> optPetSitter = petSitterDAO.getByUuid(petSitterUuid);
        //
        // if (!optPetSitter.isPresent()) {
        // throw new ApiException(ApiError.DEFAULT_MSG, "petSitter not found for uuid=" + petSitterUuid);
        // }
        //
        // PetSitter petSitter = optPetSitter.get();

        /**
         * Pets
         */
        Set<PoochCreateDTO> petCreateDTOs = petCareCreateDTO.getPooches();
        for (PoochCreateDTO petCreateDTO : petCreateDTOs) {
            String uuid = petCreateDTO.getUuid();

            if (uuid != null && !uuid.isEmpty()) {
                Optional<Pooch> optPet = poochDAO.getByUuid(uuid);

                if (!optPet.isPresent()) {
                    throw new ApiException(ApiError.DEFAULT_MSG, "Pet not found for uuid=" + uuid);
                } else {
                    Pooch pet = optPet.get();

                    if (!parent.getId().equals(pet.getParent().getId())) {
                        throw new ApiException(ApiError.DEFAULT_MSG, "Pet does not belong to petParent");
                    }
                }
            }
        }

    }

    @Override
    public Booking validateCancel(BookingCancelDTO bookingCancelDTO) {
        // TODO Auto-generated method stub
        String uuid = bookingCancelDTO.getUuid();
        if (uuid == null) {
            throw new ApiException(ApiError.DEFAULT_MSG, "uuid is required");
        }

        Optional<Booking> optBooking = bookingDAO.getByUuid(uuid);

        if (!optBooking.isPresent()) {
            throw new ApiException("Booking not found", "booking not found for uuid=" + uuid);
        }

        Booking booking = optBooking.get();

        return booking;
    }

}
