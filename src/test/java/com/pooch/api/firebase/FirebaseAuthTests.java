package com.pooch.api.firebase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterRepository;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
class FirebaseAuthTests extends IntegrationTestConfiguration {

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Test
    void test_createUser() throws FirebaseAuthException {
        // @formatter:off

        CreateRequest request = new CreateRequest()
                .setEmail(RandomGeneratorUtils.getRandomEmail())
                .setEmailVerified(false)
                .setPassword("Test1234!")
                .setPhoneNumber("+1"+RandomGeneratorUtils.getRandomPhone())
                .setDisplayName(RandomGeneratorUtils.getRandomFullName())
                .setPhotoUrl("http://www.example.com/12345678/photo.png")
                .setDisabled(false);
        // @formatter:on

        UserRecord userRecord = firebaseAuth.createUser(request);
        log.info("userRecord={}", ObjectUtils.toJson(userRecord));
        assertThat(userRecord).isNotNull();
        assertThat(userRecord.getUid()).isNotNull();

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

}
