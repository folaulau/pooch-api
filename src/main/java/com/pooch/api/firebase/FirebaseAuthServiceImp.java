package com.pooch.api.firebase;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirebaseAuthServiceImp implements FirebaseAuthService {

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Override
    public FirebaseToken verifyToken(String firebaseToken) {
        log.info("verifyToken({})", firebaseToken);
        try {
            return firebaseAuth.verifyIdToken(firebaseToken);
        } catch (FirebaseAuthException e) {
            log.warn("Firebase verify token error={}", e.getMessage());

            throw new ApiException("Unable to sign in", "Firebase verify token error=" + e.getMessage());
        }
    }

    @Override
    public Optional<UserRecord> getFirebaseUser(String uuid) {
        log.info("getFirebaseUser({})", uuid);
        UserRecord userRecord = null;
        try {
            userRecord = firebaseAuth.getUser(uuid);
        } catch (FirebaseAuthException e) {
            log.warn("Firebase getUser error={}", e.getMessage());
        }
        return Optional.ofNullable(userRecord);
    }

}
