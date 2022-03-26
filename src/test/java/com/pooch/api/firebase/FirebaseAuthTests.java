package com.pooch.api.firebase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerRepository;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
class FirebaseAuthTests extends IntegrationTestConfiguration {

    @Autowired
    private FirebaseAuth       firebaseAuth;

    @Autowired
    private FirebaseRestClient firebaseRestClient;

    @Test
    void test_createUser() throws FirebaseAuthException {
        // @formatter:off
        String password = "Test1234!";
        CreateRequest request = new CreateRequest()
                .setEmail(RandomGeneratorUtils.getRandomEmail())
                .setEmailVerified(false)
                .setPassword(password)
                .setPhoneNumber("+1"+RandomGeneratorUtils.getRandomPhone())
                .setDisplayName(RandomGeneratorUtils.getRandomFullName())
                .setPhotoUrl("http://www.example.com/12345678/photo.png")
                .setDisabled(false);
        // @formatter:on

        UserRecord userRecord = firebaseAuth.createUser(request);
        log.info("userRecord={}", ObjectUtils.toJson(userRecord));
        assertThat(userRecord).isNotNull();
        assertThat(userRecord.getUid()).isNotNull();

        // firebaseRestClient.signUp(RandomGeneratorUtils.getRandomEmail(), "Test1234!");
        FirebaseAuthResponse authResponse = firebaseRestClient.signIn(userRecord.getEmail(), password);

        log.info("idToken={}", authResponse.getIdToken());

        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getIdToken()).isNotNull();
    }

    @Test
    void test_retrieveUser() throws FirebaseAuthException {
        // @formatter:off
        CreateRequest request = new CreateRequest().setEmail(RandomGeneratorUtils.getRandomEmail())
                .setEmailVerified(false)
                .setPassword("Test1234!")
                .setPhoneNumber("+1" + RandomGeneratorUtils.getRandomPhone())
                .setDisplayName(RandomGeneratorUtils.getRandomFullName())
                .setPhotoUrl("http://www.example.com/12345678/photo.png")
                .setDisabled(false);
        // @formatter:on

        UserRecord userRecord = firebaseAuth.createUser(request);

        assertThat(userRecord).isNotNull();
        assertThat(userRecord.getUid()).isNotNull();

        String uid = userRecord.getUid();
        userRecord = firebaseAuth.getUser(uid);
        log.info("userRecord={}", ObjectUtils.toJson(userRecord));
        assertThat(userRecord).isNotNull();
        assertThat(userRecord.getUid()).isNotNull();

    }

    @Disabled
    @Test
    public void removeAllDevUsers() throws FirebaseAuthException {

        ListUsersPage listUsersPage = firebaseAuth.listUsers(null);

        while (listUsersPage != null) {
            Iterator<ExportedUserRecord> it = listUsersPage.getValues().iterator();

            while (it.hasNext()) {
                ExportedUserRecord u = it.next();

                log.info("firebase user={}", ObjectUtils.toJson(u));

                firebaseAuth.deleteUser(u.getUid());
            }

            if (listUsersPage.hasNextPage()) {
                String token = listUsersPage.getNextPageToken();

                listUsersPage = firebaseAuth.listUsers(token);
            } else {
                listUsersPage = null;
            }

            /**
             * refund booking fee
             */
        }

    }

}
