package com.pooch.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterRepository;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Disabled
@Slf4j
@SpringBootTest
class PoochApiApplicationTests {

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
        System.out.println("Successfully created new user: " + userRecord.getUid());
        log.info("userRecord={}", ObjectUtils.toJson(userRecord));

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

        String uid = userRecord.getUid();
        userRecord = firebaseAuth.getUser(uid);
        System.out.println("Successfully created new user: " + userRecord.getUid());
        log.info("userRecord={}", ObjectUtils.toJson(userRecord));

    }

}
