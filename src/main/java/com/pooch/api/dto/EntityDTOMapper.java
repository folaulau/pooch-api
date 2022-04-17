package com.pooch.api.dto;

import java.util.List;
import java.util.Set;

import com.pooch.api.elastic.repo.AddressES;
import com.pooch.api.elastic.repo.CareServiceES;
import com.pooch.api.entity.groomer.careservice.CareService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.phonenumber.PhoneNumberVerification;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.s3file.S3File;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityDTOMapper {

    Pooch mapPoochCreateDTOToPooch(PoochCreateDTO poochCreateDTO);

    PoochDTO mapPoochToPoochDTO(Pooch pooch);

    @Mappings({@Mapping(target = "uuid", ignore = true)})
    Pooch patchPet(PoochCreateDTO petCreateDTO, @MappingTarget Pooch pet);

    PhoneNumberVerificationDTO mapPhoneNumberVerificationToPhoneNumberVerificationDTO(PhoneNumberVerification phoneNumberVerification);

    ParentCreateUpdateDTO mapParentToParentCreateUpdateDTO(Parent petParent);

    GroomerUuidDTO mapGroomerToGroomerUuidDTO(Groomer groomer);

    @Mappings({@Mapping(target = "uuid", ignore = true)})
    Parent mapNewUpdateDTOToParent(ParentCreateUpdateDTO petParentUpdateDTO);

    @Mappings({@Mapping(target = "uuid", ignore = true)})
    void patchParentWithNewParentUpdateDTO(ParentCreateUpdateDTO parentCreateUpdateDTO, @MappingTarget Parent petParent);

    ParentDTO mapPetParentToPetParentDTO(Parent petParent);

    GroomerDTO mapGroomerToGroomerDTO(Groomer groomer);

    @Mappings({@Mapping(target = "uuid", ignore = true)})
    void patchGroomerWithGroomerUpdateDTO(GroomerUpdateDTO groomerUpdateDTO, @MappingTarget Groomer groomer);

    AuthenticationResponseDTO mapGroomerToAuthenticationResponse(Groomer groomer);

    AuthenticationResponseDTO mapParentToAuthenticationResponse(Parent parent);

    S3FileDTO mapS3FileToS3FileDTO(S3File s3File);

    List<S3FileDTO> mapS3FilesToS3FileDTOs(List<S3File> s3Files);

    GroomerES mapGroomerEntityToGroomerES(Groomer groomer);

    List<CareServiceES> mapCareServicesToCareServiceESs(Set<CareService> careServices);

    AddressES mapAddressToAddressEs(Address address);

    @Mappings({@Mapping(target = "uuid", ignore = true)})
    void patchCareServiceWithCareServiceUpdateDTO(CareServiceUpdateDTO careServicesDTO, @MappingTarget CareService careService);

    CareServiceDTO mapCareServiceToCareServiceDTO(CareService careService);

    CareService mapCareServiceUpdateDTOToCareService(CareServiceUpdateDTO careServicesDTO);

    Set<CareServiceDTO> mapCareServicesToCareServiceDTOs(Set<CareService> careServiceSet);

    Set<CareServiceDTO> mapCareServicesToCareServiceDTOs(List<CareService> savedCareServices);

    List<CareServiceDTO> mapCareServicesToCareServiceDTOsAsList(Set<CareService> careServices);
}
