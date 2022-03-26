package com.pooch.api.entity.groomer;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.GroomerDTO;
import com.pooch.api.dto.GroomerUpdateDTO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.entity.role.Role;
import com.pooch.api.firebase.FirebaseAuthService;
import com.pooch.api.security.AuthenticationService;
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

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO) {

        UserRecord userRecord = firebaseAuthService.verifyAndGetUser(authenticatorDTO.getToken());

        log.info("userRecord={}", ObjectUtils.toJson(userRecord));

        String uuid = userRecord.getUid();

        Optional<Groomer> optGroomer = groomerDAO.getByUuid(uuid);

        Groomer groomer = null;

        if (optGroomer.isPresent()) {
            /**
             * sign in
             */
            groomer = optGroomer.get();
        } else {
            /**
             * sign up
             */

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
                } else {
                    // temp email as placeholder
                    email = "tempParent" + RandomGeneratorUtils.getIntegerWithin(10000, Integer.MAX_VALUE) + "@poochapp.com";
                    groomer.setEmailTemp(true);
                }
            }

            groomer.setEmail(email);

            Long phoneNumber = null;

            try {
                phoneNumber = Long.parseLong(userRecord.getPhoneNumber());
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

            groomer.setPhoneNumber(phoneNumber);

            groomer = groomerDAO.save(groomer);
        }

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(groomer);

        return authenticationResponseDTO;
    }

    @Override
    public GroomerDTO updateProfile(GroomerUpdateDTO petSitterUpdateDTO) {
        Groomer petSitter = groomerValidatorService.validateUpdateProfile(petSitterUpdateDTO);

        entityDTOMapper.patchPetSitterWithPetSitterUpdateDTO(petSitterUpdateDTO, petSitter);

        petSitter = groomerDAO.save(petSitter);

        return entityDTOMapper.mapPetSitterToPetSitterDTO(petSitter);
    }
}
