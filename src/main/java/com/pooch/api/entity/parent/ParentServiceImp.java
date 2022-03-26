package com.pooch.api.entity.parent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.S3FileDTO;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.entity.role.Role;
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
public class ParentServiceImp implements ParentService {

    @Autowired
    private FirebaseAuthService    firebaseAuthService;

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
        } else {
            /**
             * sign up
             */

            petParent = new Parent();
            petParent.setUuid(userRecord.getUid());
            petParent.addRole(new Role(Authority.parent));

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
                } else {
                    // temp email as placeholder
                    email = "tempParent" + RandomGeneratorUtils.getIntegerWithin(10000, Integer.MAX_VALUE) + "@poochapp.com";
                    petParent.setEmailTemp(true);
                }
            }

            petParent.setEmail(email);

            Long phoneNumber = null;

            try {
                phoneNumber = Long.parseLong(userRecord.getPhoneNumber());
            } catch (Exception e) {
                log.warn("phoneNumber Exception, msg={}", e.getLocalizedMessage());
            }

            petParent.setPhoneNumber(phoneNumber);

            petParent = parentDAO.save(petParent);
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
            String objectKey = "parent/" + parent.getId() + "/images/" + UUID.randomUUID().toString() + "_" + santizedFileName;

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
            s3File.setParent(parent);

            s3Files.add(s3File);
        }

        if (s3Files.size() > 0) {
            s3Files = s3FileDAO.save(s3Files);
        }

        return entityDTOMapper.mapS3FilesToS3FileDTOs(s3Files);
    }

}
