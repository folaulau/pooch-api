package com.pooch.api.entity.groomer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.pooch.api.dto.*;
import com.pooch.api.elastic.groomer.GroomerESDAO;
import com.pooch.api.elastic.repo.GroomerES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.entity.role.Role;
import com.pooch.api.entity.s3file.FileType;
import com.pooch.api.entity.s3file.S3File;
import com.pooch.api.entity.s3file.S3FileDAO;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.s3.AwsS3Service;
import com.pooch.api.library.aws.s3.AwsUploadResponse;
import com.pooch.api.library.firebase.FirebaseAuthService;
import com.pooch.api.security.AuthenticationService;
import com.pooch.api.utils.FileUtils;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerServiceImp implements GroomerService {

    @Autowired
    private FirebaseAuthService     firebaseAuthService;

    @Autowired
    private GroomerDAO              groomerDAO;

    @Autowired
    private AuthenticationService   authenticationService;

    @Autowired
    private GroomerValidatorService groomerValidatorService;

    @Autowired
    private EntityDTOMapper         entityDTOMapper;

    @Autowired
    private S3FileDAO               s3FileDAO;

    @Autowired
    private AwsS3Service            awsS3Service;

    @Autowired
    private GroomerESDAO            groomerESDAO;

    @Autowired
    private CareServiceDAO          careServiceDAO;

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
            groomer.setSignUpStatus(GroomerSignUpStatus.CREATE_PROFILE);

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

    /**
     * Update all or nothing at all
     */
    @Transactional
    @Override
    public GroomerDTO updateProfile(GroomerUpdateDTO groomerUpdateDTO) {
        Groomer groomer = groomerValidatorService.validateUpdateProfile(groomerUpdateDTO);

        entityDTOMapper.patchGroomerWithGroomerUpdateDTO(groomerUpdateDTO, groomer);

        String oldEmail = groomer.getEmail();
        Long oldPhoneNumber = groomer.getPhoneNumber();

        Groomer savedGroomer = groomerDAO.save(groomer);

        String newEmail = groomerUpdateDTO.getEmail();
        Long newPhoneNumber = groomerUpdateDTO.getPhoneNumber();

        log.info("new email={}, old email={}", newEmail, oldEmail);
        log.info("new phone={}, old phone={}", newPhoneNumber, oldPhoneNumber);

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
                    careServices.remove(cs);
                });

                careServices.add(savedCareService);
            });
        }

        groomerDTO.setCareServices(entityDTOMapper.mapCareServicesToCareServiceDTOsAsList(careServices));

        /**
         * notify groomer of profile update only if status==ACTIVE
         */

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
}
