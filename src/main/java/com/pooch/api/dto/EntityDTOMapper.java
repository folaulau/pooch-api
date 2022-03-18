package com.pooch.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.pooch.api.entity.pet.Pet;
import com.pooch.api.entity.phonenumber.PhoneNumberVerification;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityDTOMapper {

    Pet mapPetCreateDTOToPet(PetCreateDTO petCreateDTO);

    PetDTO mapPetToPetDTO(Pet pet);

    @Mappings({@Mapping(target = "uuid", ignore = true)})
    Pet patchPet(PetCreateDTO petCreateDTO, @MappingTarget Pet pet);

    PhoneNumberVerificationDTO mapPhoneNumberVerificationToPhoneNumberVerificationDTO(PhoneNumberVerification phoneNumberVerification);

}