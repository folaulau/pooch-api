package com.pooch.api.entity.parent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.dto.AddressCreateUpdateDTO;
import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.ParentCancellationRequestDTO;
import com.pooch.api.dto.ParentDTO;
import com.pooch.api.dto.ParentUpdateDTO;
import com.pooch.api.dto.PhoneNumberVerificationCreateDTO;
import com.pooch.api.dto.PhoneNumberVerificationDTO;
import com.pooch.api.dto.PhoneNumberVerificationUpdateDTO;
import com.pooch.api.dto.PoochDTO;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.notification.NotificationService;
import com.pooch.api.entity.phonenumber.PhoneNumberService;
import com.pooch.api.entity.phonenumber.PhoneNumberVerification;
import com.pooch.api.entity.pooch.PoochService;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.entity.role.Role;
import com.pooch.api.entity.s3file.FileType;
import com.pooch.api.entity.s3file.S3File;
import com.pooch.api.entity.s3file.S3FileDAO;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.s3.AwsS3Service;
import com.pooch.api.library.aws.s3.AwsUploadResponse;
import com.pooch.api.library.firebase.FirebaseAuthService;
import com.pooch.api.library.stripe.customer.StripeCustomerService;
import com.pooch.api.security.AuthenticationService;
import com.pooch.api.utils.FileUtils;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParentServiceImp implements ParentService {

    @Autowired
    private FirebaseAuthService    firebaseAuthService;

    @Autowired
    private PhoneNumberService     phoneNumberService;

    @Autowired
    private ParentDAO              parentDAO;

    @Autowired
    private AuthenticationService  authenticationService;

    @Autowired
    private ParentValidatorService parentValidatorService;

    @Autowired
    private S3FileDAO              s3FileDAO;

    @Autowired
    private AwsS3Service           awsS3Service;

    @Autowired
    private EntityDTOMapper        entityDTOMapper;

    @Autowired
    private PoochService           poochService;

    @Autowired
    private NotificationService    notificationService;

    @Autowired
    private StripeCustomerService  stripeCustomerService;

    @Override
    public Parent findByUuid(String uuid) {
        return this.parentDAO.getByUuid(uuid).orElseThrow(() -> new ApiException("Parent not found", "parent not found for uuid=" + uuid));
    }

    /**
     * Sign up or sign in<br>
     * if user already in db sign in, else sign up<br>
     */
    @Override
    public AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO) {

        UserRecord userRecord = firebaseAuthService.verifyAndGetUser(authenticatorDTO.getToken());

        log.info("userRecord: uuid={}, email={}", userRecord.getUid(), userRecord.getEmail());

        Optional<Parent> optPetParent = parentDAO.getByUuid(userRecord.getUid());

        Parent petParent = null;

        if (optPetParent.isPresent()) {
            /**
             * sign in
             */
            petParent = optPetParent.get();

            if (!petParent.isActive()) {
                throw new ApiException("Your account is not active. Please contact our support team.", "status=" + petParent.getStatus());
            }

        } else {

            /**
             * sign up
             */

            petParent = new Parent();
            petParent.setUuid(userRecord.getUid());
            petParent.addRole(new Role(UserType.parent));
            petParent.setAddress(new Address());
            petParent.setStatus(ParentStatus.ACTIVE);

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
                }

            }

            if (email != null && !email.isEmpty()) {
                Optional<Parent> optEmailGroomer = parentDAO.getByEmail(email);

                if (optEmailGroomer.isPresent()) {
                    throw new ApiException("Email taken", "an account has this email already", "You are signing up with an email that is taken", "Please use one email per account");
                }
            } else {
                // temp email as placeholder
                email = "tempParent" + RandomGeneratorUtils.getIntegerWithin(10000, Integer.MAX_VALUE) + "@poochapp.com";
                petParent.setEmailTemp(true);
            }

            petParent.setFullName(userRecord.getDisplayName());

            petParent.setEmail(email);

            Long phoneNumber = null;

            try {
                phoneNumber = Long.parseLong(userRecord.getPhoneNumber());
            } catch (Exception e) {
                log.warn("phoneNumber Exception, msg={}", e.getLocalizedMessage());
            }

            petParent.setPhoneNumber(phoneNumber);

            com.stripe.model.Customer customer = stripeCustomerService.createParentDetails(petParent);

            petParent.setStripeCustomerId(customer.getId());

            try {
                petParent = parentDAO.save(petParent);
            } catch (ConstraintViolationException e) {
                throw new ApiException("issue saving ConstraintName=" + e.getConstraintName(),

                        "SQLState=" + e.getSQLState(), "ErrorCode=" + e.getErrorCode(), "SQLS=" + e.getSQL(), "SQLMessage=" + e.getMessage(), "SQLS=" + e.getSQL(),

                        "petParent=" + ObjectUtils.toJson(petParent), "userRecord=" + userRecord.toString());// TODO:

            } catch (Exception e) {
                throw new ApiException("issue saving error=" + e.getLocalizedMessage(), "petParent=" + ObjectUtils.toJson(petParent), "userRecord=" + userRecord.toString()); // exception
            }

            notificationService.sendWelcomeNotificationToParent(petParent);
        }

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(petParent);

        log.info("authenticationResponseDTO={}", ObjectUtils.toJson(authenticationResponseDTO));

        return authenticationResponseDTO;
    }

    @Override
    public List<S3FileDTO> uploadProfileImages(String uuid, List<MultipartFile> images) {
        Parent parent = parentValidatorService.validateUploadProfileImages(uuid, images);

        List<S3File> s3Files = new ArrayList<>();

        for (MultipartFile image : images) {
            String fileName = image.getOriginalFilename();
            String santizedFileName = FileUtils.replaceInvalidCharacters(fileName);
            String objectKey = "profile_images/parent/" + parent.getId() + "/" + UUID.randomUUID().toString() + "_" + santizedFileName;

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
            s3File.setFileType(FileType.PROFILE_IMAGE);
            s3File.setParent(parent);

            s3Files.add(s3File);
        }

        if (s3Files.size() > 0) {
            s3Files = s3FileDAO.save(s3Files);
            /**
             * set last profile image as main profile image
             */
            s3FileDAO.setMainProfileImage(parent, s3Files.get(s3Files.size() - 1));
        }

        return entityDTOMapper.mapS3FilesToS3FileDTOs(s3Files);
    }

    @Override
    public void signOut(String token) {
        // TODO Auto-generated method stub
    }

    @Override
    public ParentDTO updateProfile(ParentUpdateDTO parentUpdateDTO) {

        Parent parent = parentValidatorService.validateUpdateProfile(parentUpdateDTO);

        log.info("parent={}", ObjectUtils.toJson(parent));

        entityDTOMapper.patchParentWithParentUpdateDTO(parentUpdateDTO, parent);

        log.info("parent1={}", ObjectUtils.toJson(parent));

        AddressCreateUpdateDTO addressCreateUpdateDTO = parentUpdateDTO.getAddress();

        if (addressCreateUpdateDTO != null) {
            Address address = parent.getAddress();

            if (address == null) {
                address = new Address();
            }

            entityDTOMapper.patchAddressWithAddressCreateUpdateDTO(addressCreateUpdateDTO, address);

            address.setParent(parent);
            parent.setAddress(address);
        }

        parent = parentDAO.save(parent);

        ParentDTO parentDTO = entityDTOMapper.mapPetParentToPetParentDTO(parent);

        List<PoochDTO> pooches = poochService.updatePooches(parent, parentUpdateDTO.getPooches());

        log.info("pooches={}", ObjectUtils.toJson(pooches));

        parentDTO.setPooches(pooches);

        return parentDTO;
    }

    @Override
    public ApiDefaultResponseDTO requestPhoneNumberVerification(String uuid, PhoneNumberVerificationCreateDTO phoneNumberRequestVerificationDTO) {

        Parent parent = findByUuid(uuid);

        return this.phoneNumberService.requestVerification(parent, phoneNumberRequestVerificationDTO);
    }

    @Override
    public PhoneNumberVerificationDTO verifyNumberWithCode(String uuid, PhoneNumberVerificationUpdateDTO phoneNumberVerificationDTO) {

        Parent parent = findByUuid(uuid);

        PhoneNumberVerification phoneNumberVerification = phoneNumberService.verifyNumberWithCode(parent, phoneNumberVerificationDTO);

        if (phoneNumberVerification.getPhoneVerified() != null && phoneNumberVerification.getPhoneVerified() == true) {
            parent.setPhoneNumber(phoneNumberVerification.getPhoneNumber());
            parent.setPhoneNumberVerified(true);
            parent.setPhoneNumberVerification(phoneNumberVerification);

            this.parentDAO.save(parent);

        }

        return entityDTOMapper.mapPhoneNumberVerificationToPhoneNumberVerificationDTO(phoneNumberVerification);
    }

    @Override
    public S3FileDTO uploadProfileImage(String uuid, MultipartFile image) {
        Parent parent = parentValidatorService.validateUploadProfileImage(uuid, image);

        String fileName = image.getOriginalFilename();
        String santizedFileName = FileUtils.replaceInvalidCharacters(fileName);
        String objectKey = "profile_images/parent/" + parent.getId() + "/" + UUID.randomUUID().toString() + "_" + santizedFileName;

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
        s3File.setFileType(FileType.PROFILE_IMAGE);
        s3File.setParent(parent);
        s3File.setMainProfileImage(true);
        /**
         * set first profile image as main profile image
         */

        s3File = s3FileDAO.setMainProfileImage(parent, s3File);

        return entityDTOMapper.mapS3FileToS3FileDTO(s3File);
    }

    @Override
    public boolean cancelAccount(ParentCancellationRequestDTO cancellationRequest) {
        /**
         * Cannot be cancelled if:
         * 
         * 1. have outstanding balance
         * 
         * 2. find out more!
         * 
         * 
         */

        /**
         * Result of cancellation:
         * 
         * 1. Parents cannot login again
         * 
         * 2. Parents still show up in Groomers dashboard for historical records not for new appointments.
         */

        Parent parent = parentValidatorService.validateAccountCancellation(cancellationRequest);

        parent.cancelAccount(cancellationRequest.getReason());

        parent = this.parentDAO.save(parent);

        return true;
    }

}
