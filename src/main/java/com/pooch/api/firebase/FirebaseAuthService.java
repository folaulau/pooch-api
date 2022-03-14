package com.pooch.api.firebase;

import java.util.Optional;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

public interface FirebaseAuthService {

    FirebaseToken verifyToken(String firebaseToken);

    Optional<UserRecord> getFirebaseUser(String uuid);
}
