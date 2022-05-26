package com.pooch.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

@Profile(value = {"local", "github", "dev", "qa", "prod"})
@Configuration
public class FirebaseConfig {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${firebase.key.file.location}")
    private String firebaseKeyLocation;

    @Bean
    public FirebaseApp firebaseApp() {
        FirebaseApp firebaseApp = null;
        try {
            if (FirebaseApp.getInstance() != null) {
                firebaseApp = FirebaseApp.getInstance();
                log.info("MyFirebase config had already been set up, name: " + firebaseApp.getName());
                return firebaseApp;
            } else {
                log.info("MyFirebase config is null. Configuring one...");
            }
        } catch (Exception e) {
            log.warn("FirebaseApp Exception, msg: " + e.getLocalizedMessage());
        }
        try {
            // @formatter:off

			FirebaseOptions options = FirebaseOptions.builder()
			        .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseKeyLocation).getInputStream()))
                    .build();
			
			// @formatter:on
            firebaseApp = FirebaseApp.initializeApp(options);
            log.info("MyFirebase config has been set up, name: " + firebaseApp.getName());
            return firebaseApp;
        } catch (Exception e) {
            log.error("FirebaseApp Exception, msg: " + e.getLocalizedMessage());
        }
        log.info("FirebaseApp is null. Do something...");
        return firebaseApp;
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance(firebaseApp());
    }
    
    @DependsOn(value = "firebaseApp")
    @Bean
    public Firestore firestore() {
      return FirestoreClient.getFirestore();
    }
}
