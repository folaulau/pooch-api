package com.pooch.api.entity.groomer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.pooch.api.dto.*;
import com.pooch.api.elastic.groomer.GroomerESDAO;
import com.pooch.api.elastic.repo.GroomerES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.address.AddressDAO;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.groomer.careservice.type.GroomerServiceCategory;
import com.pooch.api.entity.groomer.careservice.type.GroomerServiceTypeService;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.entity.role.Role;
import com.pooch.api.entity.s3file.FileType;
import com.pooch.api.entity.s3file.S3File;
import com.pooch.api.entity.s3file.S3FileDAO;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.s3.AwsS3Service;
import com.pooch.api.library.aws.s3.AwsUploadResponse;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.firebase.FirebaseAuthService;
import com.pooch.api.library.stripe.account.StripeAccountService;
import com.pooch.api.security.AuthenticationService;
import com.pooch.api.utils.FileUtils;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.AccountLink;
import com.stripe.net.RequestOptions;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.AccountRetrieveParams;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerServiceImp implements GroomerService {

    @Autowired
    private FirebaseAuthService       firebaseAuthService;

    @Autowired
    private GroomerDAO                groomerDAO;

    @Autowired
    private AuthenticationService     authenticationService;

    @Autowired
    private GroomerValidatorService   groomerValidatorService;

    @Autowired
    private EntityDTOMapper           entityDTOMapper;

    @Autowired
    private S3FileDAO                 s3FileDAO;

    @Autowired
    private AwsS3Service              awsS3Service;

    @Autowired
    private GroomerESDAO              groomerESDAO;

    @Autowired
    private CareServiceDAO            careServiceDAO;

    @Autowired
    private AddressDAO                addressDAO;

    @Autowired
    private GroomerServiceTypeService groomerServiceTypeService;

    @Autowired
    @Qualifier(value = "stripeSecrets")
    private StripeSecrets             stripeSecrets;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Value("${spring.profiles.active}")
    private String                    env;

    @Autowired
    private StripeAccountService      stripeAccountService;

    @Autowired
    private GroomerAuditService       groomerAuditService;

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO) {

        UserRecord userRecord = firebaseAuthService.verifyAndGetUser(authenticatorDTO.getToken());

        log.info("userRecord={}", ObjectUtils.toJson(userRecord));

        String uuid = userRecord.getUid();

        Optional<Groomer> optGroomer = groomerDAO.getByUuid(uuid);

        Groomer groomer = null;

        if (optGroomer.isPresent()) {
            /** sign in */
            groomer = optGroomer.get();

            if (!groomer.isAllowedToLogin()) {
                log.info("{} can't sign in because for this reason=", groomer.getFullName(), groomer.getStatus().getDisAllowedToLoginReason());
                throw new ApiException(groomer.getStatus().getDisAllowedToLoginReason());
            }
        } else {
            /** sign up */
            groomer = new Groomer();

            groomer.setUuid(uuid);
            groomer.addRole(new Role(Authority.groomer));

            String email = userRecord.getEmail();

            if (email == null || email.isEmpty()) {
                UserInfo[] userInfos = userRecord.getProviderData();

                Optional<String> optEmail = Arrays.asList(userInfos)
                        .stream()
                        .filter(userInfo -> (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()))
                        .map(userInfo -> userInfo.getEmail())
                        .findFirst();

                if (optEmail.isPresent()) {
                    email = optEmail.get();

                    Optional<Groomer> optEmailGroomer = groomerDAO.getByEmail(email);
                    if (optEmailGroomer.isPresent()) {
                        throw new ApiException("Email taken", "an account has this email already", "Please use one email per account");
                    }
                } else {
                    // temp email as placeholder
                    email = "tempParent" + RandomGeneratorUtils.getIntegerWithin(10000, Integer.MAX_VALUE) + "@poochapp.com";
                    groomer.setEmailTemp(true);
                }
            }

            groomer.setEmail(email);
            groomer.setStatus(GroomerStatus.SIGNING_UP);
            groomer.setSignUpStatus(GroomerSignUpStatus.SIGNED_UP);

            Long phoneNumber = null;

            try {
                phoneNumber = Long.parseLong(userRecord.getPhoneNumber());
            } catch (Exception e) {
                log.warn("phoneNumber Exception, msg={}", e.getLocalizedMessage());
            }

            groomer.setPhoneNumber(phoneNumber);

            log.info("groomer={}", ObjectUtils.toJson(groomer));

            groomer = groomerDAO.save(groomer);
        }

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(groomer);

        log.info("authenticationResponseDTO={}", ObjectUtils.toJson(authenticationResponseDTO));

        return authenticationResponseDTO;
    }

    @Override
    public Groomer findByUuid(String uuid) {
        return this.groomerDAO.getByUuid(uuid).orElseThrow(() -> new ApiException("Groomer not found", "groomer not found for uuid=" + uuid));
    }

    @Override
    public GroomerDTO createUpdateProfile(GroomerCreateProfileDTO groomerCreateProfileDTO) {
        Groomer groomer = groomerValidatorService.validateCreateUpdateProfile(groomerCreateProfileDTO);

        entityDTOMapper.patchGroomerWithGroomerCreateProfileDTO(groomerCreateProfileDTO, groomer);

        Set<Address> addresses = groomer.getAddresses();

        AddressCreateUpdateDTO addressDTO = groomerCreateProfileDTO.getAddress();

        if (addressDTO != null) {
            String addressUuid = addressDTO.getUuid();

            Address address = null;
            if (addressUuid != null && !addressUuid.trim().isEmpty()) {
                address = addresses.stream().filter(addr -> addr.getUuid().equalsIgnoreCase(addressUuid)).findFirst().get();
                entityDTOMapper.patchAddressWithAddressCreateUpdateDTO(addressDTO, address);
                address.setGroomer(groomer);
            } else {
                address = entityDTOMapper.mapAddressCreateUpdateDTOToAddress(addressDTO);
                address.setGroomer(groomer);
                addresses.add(address);
            }
        }

        groomer.setAddresses(addresses);

        groomer.setSignUpStatus(GroomerSignUpStatus.PROFILE_CREATED);

        Groomer savedGroomer = groomerDAO.save(groomer);

        GroomerDTO groomerDTO = entityDTOMapper.mapGroomerToGroomerDTO(savedGroomer);

        /**
         * Update careServices
         */

        final Set<CareService> careServices = careServiceDAO.findByGroomerId(groomer.getId()).orElse(new HashSet<>());
        final Set<Long> careServicesToRemove = (careServices.size() > 0 ? careServices.stream().map(careService -> careService.getId()).collect(Collectors.toSet()) : new HashSet<>());
        Set<CareService> dbCareServices = careServices;

        Set<CareServiceUpdateDTO> careServicesDTOs = groomerCreateProfileDTO.getCareServices();

        if (null != careServicesDTOs) {
            careServicesDTOs.stream().forEach(careServicesDTO -> {

                String careServiceUuid = careServicesDTO.getUuid();

                CareService careService = null;

                if (careServiceUuid != null && !careServiceUuid.trim().isEmpty()) {
                    careService = careServiceDAO.getByUuid(careServicesDTO.getUuid()).get();
                    careServicesToRemove.remove(careService.getId());
                    entityDTOMapper.patchCareServiceWithCareServiceUpdateDTO(careServicesDTO, careService);
                } else {
                    careService = entityDTOMapper.mapCareServiceUpdateDTOToCareService(careServicesDTO);
                }

                careService.setGroomer(savedGroomer);

                CareService savedCareService = careServiceDAO.save(careService);

                /**
                 * remove stale CareService
                 */
                careServices.stream().filter(cs -> cs.getId().equals(savedCareService.getId())).findFirst().ifPresent(cs -> {
                    log.info("remove state cs");
                    careServices.remove(cs);
                });

                careServices.add(savedCareService);
            });
        }

        log.info("careServicesToRemove={}",ObjectUtils.toJson(careServicesToRemove)); 
        
        List<GroomerServiceCategory> careServiceTypes = groomerServiceTypeService.getTopServiceTypes(4L);
        
        log.info("careServiceTypes={}",ObjectUtils.toJson(careServiceTypes)); 

        Set<Long> careServiceIdsToRemove = dbCareServices.stream().filter(cs -> {
            if (careServicesToRemove.contains(cs.getId()) && canRemoveTopCareService(careServiceTypes, cs)) {
                return true;
            } else {
                return false;
            }
        }).map(cs -> cs.getId()).collect(Collectors.toSet());
        
        log.info("careServiceIdsToRemove={}",ObjectUtils.toJson(careServiceIdsToRemove));        
        /**
         * delete careServices that are not passed
         */
        if (careServicesToRemove.size() > 0) {
            careServiceDAO.deleteByIds(careServiceIdsToRemove);
        }

        Set<CareService> savedSareServices = careServices.stream().filter(careService -> {
            if (careServicesToRemove.contains(careService.getId())) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toSet());

        groomerDTO.setCareServices(entityDTOMapper.mapCareServicesToCareServiceDTOs(savedSareServices));

        applicationEventPublisher.publishEvent(new GroomerUpdateEvent(new GroomerEvent(groomer.getId())));

        groomerAuditService.audit(savedGroomer);

        return groomerDTO;
    }

    private boolean canRemoveTopCareService(List<GroomerServiceCategory> careServiceTypes, CareService careService) {
        return careServiceTypes.stream().filter(cst -> {
            if (cst.getName().equalsIgnoreCase(careService.getName())) {
                return true;
            } else {
                return false;
            }
        }).findFirst().isPresent();
    }

    /**
     * Only save ones passed<br>
     * Delete any not pass
     */
    @Override
    public GroomerDTO createUpdateListing(GroomerCreateListingDTO groomerCreateListingDTO) {
        Groomer groomer = groomerValidatorService.validateCreateUpdateListing(groomerCreateListingDTO);

        entityDTOMapper.patchGroomerWithGroomerCreateListingDTO(groomerCreateListingDTO, groomer);

        groomer.setSignUpStatus(GroomerSignUpStatus.LISTING_CREATED);

        Groomer savedGroomer = groomerDAO.save(groomer);

        GroomerDTO groomerDTO = entityDTOMapper.mapGroomerToGroomerDTO(savedGroomer);

        /**
         * Update careServices
         */

        final Set<CareService> careServices = careServiceDAO.findByGroomerId(groomer.getId()).orElse(new HashSet<>());
        final Set<Long> careServicesToRemove = (careServices.size() > 0 ? careServices.stream().map(careService -> careService.getId()).collect(Collectors.toSet()) : new HashSet<>());

        Set<CareServiceUpdateDTO> careServicesDTOs = groomerCreateListingDTO.getCareServices();

        if (null != careServicesDTOs) {
            careServicesDTOs.stream().forEach(careServicesDTO -> {

                String careServiceUuid = careServicesDTO.getUuid();

                CareService careService = null;

                if (careServiceUuid != null && !careServiceUuid.trim().isEmpty()) {
                    careService = careServiceDAO.getByUuid(careServicesDTO.getUuid()).get();
                    careServicesToRemove.remove(careService.getId());
                    entityDTOMapper.patchCareServiceWithCareServiceUpdateDTO(careServicesDTO, careService);
                } else {
                    careService = entityDTOMapper.mapCareServiceUpdateDTOToCareService(careServicesDTO);
                }

                careService.setGroomer(savedGroomer);

                CareService savedCareService = careServiceDAO.save(careService);

                /**
                 * remove stale CareService
                 */
                careServices.stream().filter(cs -> cs.getId().equals(savedCareService.getId())).findFirst().ifPresent(cs -> {
                    log.info("remove state cs");
                    careServices.remove(cs);
                });

                careServices.add(savedCareService);
            });
        }

        /**
         * delete careServices that are not passed
         */
        if (careServicesToRemove.size() > 0) {
            careServiceDAO.deleteByIds(careServicesToRemove);
        }

        Set<CareService> savedSareServices = careServices.stream().filter(careService -> {
            if (careServicesToRemove.contains(careService.getId())) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toSet());

        groomerDTO.setCareServices(entityDTOMapper.mapCareServicesToCareServiceDTOs(savedSareServices));

        /**
         * notify groomer of profile update only if status==ACTIVE
         */

        applicationEventPublisher.publishEvent(new GroomerUpdateEvent(new GroomerEvent(groomer.getId())));

        groomerAuditService.audit(savedGroomer);

        return groomerDTO;
    }

    /**
     * Update all or nothing at all
     */
    @Transactional
    @Override
    public GroomerDTO updateProfile(GroomerUpdateDTO groomerUpdateDTO) {
        Groomer groomer = groomerValidatorService.validateUpdateProfile(groomerUpdateDTO);

        entityDTOMapper.patchGroomerWithGroomerUpdateDTO(groomerUpdateDTO, groomer);

        Long oldPhoneNumber = groomer.getPhoneNumber();

        Long newPhoneNumber = groomerUpdateDTO.getPhoneNumber();

        log.info("new phone={}, old phone={}", newPhoneNumber, oldPhoneNumber);

        /**
         * Update addresses
         */

        Set<Address> addresses = groomer.getAddresses();

        Set<AddressCreateUpdateDTO> addressDTOs = groomerUpdateDTO.getAddresses();

        if (addressDTOs != null) {
            addressDTOs.stream().forEach(addressCreateUpdateDTO -> {
                String addressUuid = addressCreateUpdateDTO.getUuid();

                Address address = null;
                if (addressUuid != null && !addressUuid.trim().isEmpty()) {
                    address = addresses.stream().filter(addr -> addr.getUuid().equalsIgnoreCase(addressUuid)).findFirst().get();
                    entityDTOMapper.patchAddressWithAddressCreateUpdateDTO(addressCreateUpdateDTO, address);
                    address.setGroomer(groomer);
                } else {
                    address = entityDTOMapper.mapAddressCreateUpdateDTOToAddress(addressCreateUpdateDTO);
                    address.setGroomer(groomer);
                    addresses.add(address);
                }

            });
        }

        groomer.setAddresses(addresses);

        Groomer savedGroomer = groomerDAO.save(groomer);

        GroomerDTO groomerDTO = entityDTOMapper.mapGroomerToGroomerDTO(groomer);

        /**
         * Update careServices
         */

        Set<CareService> careServices = careServiceDAO.findByGroomerId(groomer.getId()).orElse(new HashSet<>());

        Set<CareServiceUpdateDTO> careServicesDTOs = groomerUpdateDTO.getCareServices();

        if (null != careServicesDTOs) {
            careServicesDTOs.stream().forEach(careServicesDTO -> {

                String careServiceUuid = careServicesDTO.getUuid();

                CareService careService = null;

                if (careServiceUuid != null && !careServiceUuid.trim().isEmpty()) {
                    careService = careServiceDAO.getByUuid(careServicesDTO.getUuid()).get();
                    entityDTOMapper.patchCareServiceWithCareServiceUpdateDTO(careServicesDTO, careService);
                } else {
                    careService = entityDTOMapper.mapCareServiceUpdateDTOToCareService(careServicesDTO);
                }

                careService.setGroomer(savedGroomer);

                CareService savedCareService = careServiceDAO.save(careService);

                /**
                 * remove stale CareService
                 */
                careServices.stream().filter(cs -> cs.getId().equals(savedCareService.getId())).findFirst().ifPresent(cs -> {
                    log.info("remove state cs");
                    careServices.remove(cs);
                });

                careServices.add(savedCareService);
            });
        }

        groomerDTO.setCareServices(entityDTOMapper.mapCareServicesToCareServiceDTOs(careServices));

        /**
         * notify groomer of profile update only if status==ACTIVE
         */

        applicationEventPublisher.publishEvent(new GroomerUpdateEvent(new GroomerEvent(groomer.getId())));

        groomerAuditService.audit(savedGroomer);

        return groomerDTO;
    }

    @Override
    public List<S3FileDTO> uploadProfileImages(String uuid, List<MultipartFile> images) {
        Groomer groomer = groomerValidatorService.validateUploadProfileImages(uuid, images);

        List<S3File> s3Files = new ArrayList<>();

        for (MultipartFile image : images) {
            String fileName = image.getOriginalFilename();
            String santizedFileName = FileUtils.replaceInvalidCharacters(fileName);
            String objectKey = "profile_images/groomer/" + groomer.getId() + "/" + UUID.randomUUID().toString() + "_" + santizedFileName;

            log.info("fileName={}, santizedFileName={}, objectKey={}", fileName, santizedFileName, objectKey);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());

            AwsUploadResponse awsUploadResponse = null;
            try {
                awsUploadResponse = awsS3Service.uploadPublicObj(objectKey, metadata, image.getInputStream());
            } catch (IOException e) {
                log.warn("Issue uploading image, localMsg={}", e.getLocalizedMessage());
                e.printStackTrace();

                throw new ApiException("Unable to upload image", "issue with aws");
            }

            S3File s3File = new S3File(fileName, awsUploadResponse.getObjectKey(), awsUploadResponse.getObjectUrl());
            s3File.setFileType(FileType.Profile_Image);
            s3File.setGroomer(groomer);

            s3Files.add(s3File);
        }

        if (s3Files.size() > 0) {
            s3Files = s3FileDAO.save(s3Files);
        }

        groomerAuditService.auditAsync(groomer);

        return entityDTOMapper.mapS3FilesToS3FileDTOs(s3Files);
    }

    @Override
    public List<S3FileDTO> uploadContractDocuments(String uuid, List<MultipartFile> images) {
        Groomer groomer = groomerValidatorService.validateUploadContracts(uuid, images);

        List<S3File> s3Files = new ArrayList<>();

        for (MultipartFile image : images) {
            String fileName = image.getOriginalFilename();
            String santizedFileName = FileUtils.replaceInvalidCharacters(fileName);
            String objectKey = "contracts/groomer/" + groomer.getId() + "/" + UUID.randomUUID().toString() + "_" + santizedFileName;

            log.info("fileName={}, santizedFileName={}, objectKey={}", fileName, santizedFileName, objectKey);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());

            AwsUploadResponse awsUploadResponse = null;
            try {
                awsUploadResponse = awsS3Service.uploadPrivateObj(objectKey, metadata, image.getInputStream());
            } catch (IOException e) {
                log.warn("Issue uploading image, localMsg={}", e.getLocalizedMessage());
                e.printStackTrace();

                throw new ApiException("Unable to upload file", "issue with aws");
            }

            S3File s3File = new S3File(fileName, awsUploadResponse.getObjectKey(), awsUploadResponse.getObjectUrl());
            s3File.setFileType(FileType.Contract_Attachment);
            s3File.setIsPublic(false);
            s3File.setGroomer(groomer);

            s3Files.add(s3File);
        }

        if (s3Files.size() > 0) {
            s3Files = s3FileDAO.save(s3Files);
        }

        groomerAuditService.auditAsync(groomer);

        return entityDTOMapper.mapS3FilesToS3FileDTOs(s3Files);
    }

    @Override
    public CustomPage<GroomerES> search(GroomerSearchParamsDTO filters) {
        groomerValidatorService.validateSearch(filters);
        return groomerESDAO.search(filters);
    }

    @Override
    public ApiDefaultResponseDTO signOut(String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GroomerDTO syncStripeInfo(String uuid) {

        Groomer groomer = findByUuid(uuid);
        /**
         * get stripe account and update groomer
         */

        com.stripe.model.Account account = stripeAccountService.getById(groomer.getStripeConnectedAccountId());

        groomer = syncStripeAccountWithGroomer(groomer, account);

        groomer = this.groomerDAO.save(groomer);

        groomerAuditService.auditAsync(groomer);

        return this.entityDTOMapper.mapGroomerToGroomerDTO(groomer);
    }

    @Override
    public StripeAccountLinkDTO getStripeAccountLink(String uuid, String host) {

        Stripe.apiKey = stripeSecrets.getSecretKey();

        Groomer groomer = this.findByUuid(uuid);

        if (!groomer.getSignUpStatus().equals(GroomerSignUpStatus.COMPLETED)) {
            throw new ApiException("You have not finished signing up.", "signUpStatus=" + groomer.getSignUpStatus());
        }

        com.stripe.model.Account account = null;

        if (groomer.getStripeConnectedAccountId() == null) {
            account = stripeAccountService.create(groomer);
            groomer.setStripeConnectedAccountId(account.getId());

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }

        } else {
            account = stripeAccountService.getById(groomer.getStripeConnectedAccountId());

        }

        groomer = syncStripeAccountWithGroomer(groomer, account);

        groomer = this.groomerDAO.save(groomer);

        // only take account_onboarding for now
        AccountLink accountLink = stripeAccountService.getByAccountId(groomer.getStripeConnectedAccountId(), AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING, host);

        return new StripeAccountLinkDTO(LocalDateTime.ofInstant(Instant.ofEpochSecond(accountLink.getExpiresAt()), TimeZone.getDefault().toZoneId()), accountLink.getUrl());

    }

    private Groomer syncStripeAccountWithGroomer(Groomer groomer, com.stripe.model.Account account) {
        groomer.setStripeChargesEnabled(account.getChargesEnabled());
        groomer.setStripeDetailsSubmitted(account.getDetailsSubmitted());
        groomer.setStripePayoutsEnabled(account.getPayoutsEnabled());
        groomer.setStripeAcceptCardPayments(account.getCapabilities().getCardPayments());
        return groomer;
    }
}
