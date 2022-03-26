package com.pooch.api.library.firebase;

import java.util.Optional;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

public interface FirebaseAuthService {

    FirebaseToken verifyToken(String firebaseToken);

    Optional<UserRecord> getFirebaseUser(String uuid);

    UserRecord verifyAndGetUser(String firebaseToken);
}
