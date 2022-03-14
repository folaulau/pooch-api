package com.pooch.api.entity.petparent;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.firebase.FirebaseAuthService;
import com.pooch.api.security.AuthenticationService;

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
        FirebaseToken firebaseToken = firebaseAuthService.verifyToken(authenticatorDTO.getToken());

        Optional<UserRecord> optUserRecord = firebaseAuthService.getFirebaseUser(firebaseToken.getUid());

        UserRecord userRecord = optUserRecord.get();

        Optional<PetParent> optPetParent = petParentDAO.getByUuid(userRecord.getUid());

        PetParent petParent = null;

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
            petParent.setFullName(userRecord.getDisplayName());
            petParent.setEmail(userRecord.getEmail());
            petParent.setPhoneNumber(userRecord.getPhoneNumber());

            petParentDAO.save(petParent);
        }

        return authenticationService.authenticate(petParent);
    }

}
