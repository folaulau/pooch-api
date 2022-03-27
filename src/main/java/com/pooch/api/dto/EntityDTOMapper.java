package com.pooch.api.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.pet.Pet;
import com.pooch.api.entity.phonenumber.PhoneNumberVerification;
import com.pooch.api.entity.s3file.S3File;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityDTOMapper {

	Pet mapPetCreateDTOToPet(PetCreateDTO petCreateDTO);

	PetDTO mapPetToPetDTO(Pet pet);

	@Mappings({ @Mapping(target = "uuid", ignore = true) })
	Pet patchPet(PetCreateDTO petCreateDTO, @MappingTarget Pet pet);

	PhoneNumberVerificationDTO mapPhoneNumberVerificationToPhoneNumberVerificationDTO(
			PhoneNumberVerification phoneNumberVerification);

	PetParentUpdateDTO mapPetParentToPetParentUpdateDTO(Parent petParent);

	GroomerUuidDTO mapPetSitterToPetSitterUuidDTO(Groomer petSitter);

	@Mappings({ @Mapping(target = "uuid", ignore = true) })
	Parent mapPetParentUpdateDTOToPetParent(PetParentUpdateDTO petParentUpdateDTO);

	@Mappings({ @Mapping(target = "uuid", ignore = true) })
	void patchPetParentWithPetParentUpdateDTO(PetParentUpdateDTO petParentUpdateDTO, @MappingTarget Parent petParent);

	PetParentDTO mapPetParentToPetParentDTO(Parent petParent);

	GroomerDTO mapPetSitterToPetSitterDTO(Groomer petSitter);

	@Mappings({ @Mapping(target = "uuid", ignore = true) })
	void patchPetSitterWithPetSitterUpdateDTO(GroomerUpdateDTO petSitterUpdateDTO, @MappingTarget Groomer petSitter);

	AuthenticationResponseDTO mapGroomerToAuthenticationResponse(Groomer groomer);

	AuthenticationResponseDTO mapParentToAuthenticationResponse(Parent parent);

	S3FileDTO mapS3FileToS3FileDTO(S3File s3File);

	List<S3FileDTO> mapS3FilesToS3FileDTOs(List<S3File> s3Files);

	GroomerES mapGroomerEntityToGroomerES(Groomer groomer);

}
