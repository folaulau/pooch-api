package com.pooch.api.entity.parent;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.entity.role.Role;
import com.pooch.api.firebase.FirebaseAuthService;
import com.pooch.api.security.AuthenticationService;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParentServiceImp implements ParentService {

    @Autowired
    private FirebaseAuthService   firebaseAuthService;

    @Autowired
    private ParentDAO             parentDAO;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Sign up or sign in<br>
     * if user already in db sign in, else sign up<br>
     */
    @Override
    public AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO) {

        UserRecord userRecord = firebaseAuthService.verifyAndGetUser(authenticatorDTO.getToken());

        log.info("userRecord={}", ObjectUtils.toJson(userRecord));

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

}
