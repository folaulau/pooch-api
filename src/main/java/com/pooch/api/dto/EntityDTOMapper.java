package com.pooch.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.pooch.api.entity.pet.Pet;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityDTOMapper {

    Pet mapPetCreateDTOToPet(PetCreateDTO petCreateDTO);

    PetDTO mapPetToPetDTO(Pet pet);

    @Mappings({@Mapping(target = "id", ignore = true), 
        @Mapping(target = "uuid", ignore = true),
            @Mapping(target = "deleted", ignore = true)})
    void patchPet(PetCreateDTO petCreateDTO, @MappingTarget Pet pet);

}
