package com.pooch.api.entity.petparent;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.entity.role.Role;
import com.pooch.api.firebase.FirebaseAuthService;
import com.pooch.api.security.AuthenticationService;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetParentServiceImp implements PetParentService {

    @Autowired
    private FirebaseAuthService   firebaseAuthService;

    @Autowired
    private PetParentDAO          petParentDAO;

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

        Optional<PetParent> optPetParent = petParentDAO.getByUuid(userRecord.getUid());

        PetParent petParent = null;

        boolean signUp = false;

        if (optPetParent.isPresent()) {
            /**
             * sign in
             */
            petParent = optPetParent.get();
        } else {
            /**
             * sign up
             */

            petParent = new PetParent();
            petParent.setUuid(userRecord.getUid());
            petParent.setEmail(userRecord.getEmail());
            petParent.addRole(new Role(Authority.pet_parent));

            Long phoneNumber = null;

            try {
                phoneNumber = Long.parseLong(userRecord.getPhoneNumber());
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

            petParent.setPhoneNumber(phoneNumber);

            petParent = petParentDAO.save(petParent);

            signUp = true;
        }

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(petParent);
        authenticationResponseDTO.setSignUp(signUp);
        authenticationResponseDTO.setSignIn(!signUp);

        return authenticationResponseDTO;
    }

}
