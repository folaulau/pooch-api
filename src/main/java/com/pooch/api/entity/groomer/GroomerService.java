package com.pooch.api.entity.groomer;

import java.util.List;
import javax.validation.Valid;
import com.pooch.api.dto.*;
import com.pooch.api.elastic.repo.GroomerES;
import org.springframework.web.multipart.MultipartFile;

public interface GroomerService {

    Groomer findByUuid(String uuid);

    AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

    List<S3FileDTO> uploadProfileImages(String uuid, List<MultipartFile> images);

    List<S3FileDTO> uploadContractDocuments(String uuid, List<MultipartFile> images);

    CustomPage<GroomerES> search(GroomerSearchParamsDTO filters);

    ApiDefaultResponseDTO signOut(String token);

    StripeAccountLinkDTO getStripeAccountLink(String uuid, String host);

    GroomerDTO syncStripeInfo(String uuid);

    GroomerDTO createUpdateProfile(GroomerCreateProfileDTO groomerCreateProfileDTO);

    GroomerDTO createListing(GroomerCreateListingDTO groomerCreateListingDTO);
    
    GroomerDTO updateListing(GroomerUpdateListingDTO groomerUpdateListingDTO);

    GroomerDTO toggleListing(GroomerListingUpdateDTO listingUpdateDTO);

    GroomerDTO updateSettings(SettingsUpdateDTO settingsUpdateDTO);

    GroomerDTO createUpdateAvailability(GroomerAvailabilityCreateUpdateDTO groomerAvailabilityCreateUpdateDTO);

    GroomerDTO updateBanListing(@Valid BanListingDTO banListingDTO);

    SubscribedDTO subscribe(SubscriberCreateDTO subscribeCreateDTO);
}
