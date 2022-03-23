package com.pooch.api.firebase;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirebaseRestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${firebase.web.api.key}")
    private String       firebaseWebApiKey;

    public FirebaseAuthResponse signUp(String email, String password, boolean returnSecureToken) {
        log.info("signUp()");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> authObject = new HashMap<>();
        authObject.put("email", email);
        authObject.put("password", password);
        authObject.put("returnSecureToken", returnSecureToken);

        HttpEntity<String> entity = new HttpEntity<>(ObjectUtils.toJson(authObject), headers);

        Map<String, String> pathVariables = new HashMap<String, String>();

        String url = UriComponentsBuilder.fromHttpUrl("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + firebaseWebApiKey).buildAndExpand(pathVariables).toUriString();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<FirebaseAuthResponse> response = null;
        FirebaseAuthResponse authResponse = null;
        try {

            response = restTemplate.exchange(new URI(url), HttpMethod.POST, entity, FirebaseAuthResponse.class);

            authResponse = response.getBody();

            // log.info("authResponse: {}", ObjectUtils.toJson(authResponse));
        } catch (Exception e) {
            log.warn("Exception, msg: {}", e.getMessage());
            log.warn(ObjectUtils.toJson(response));
        }

        return authResponse;
    }

    public FirebaseAuthResponse signUp(String email, String password) {
        return signUp(email, password, true);
    }

    public FirebaseAuthResponse signIn(String email, String password, boolean returnSecureToken) {
        log.info("signIn()");

        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> authObject = new HashMap<>();
        authObject.put("email", email);
        authObject.put("password", password);
        authObject.put("returnSecureToken", returnSecureToken);

        HttpEntity<String> entity = new HttpEntity<>(ObjectUtils.toJson(authObject), headers);

        Map<String, String> pathVariables = new HashMap<String, String>();

        String url = UriComponentsBuilder.fromHttpUrl("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseWebApiKey).buildAndExpand(pathVariables).toUriString();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<FirebaseAuthResponse> response = null;
        FirebaseAuthResponse authResponse = null;
        try {

            response = restTemplate.exchange(new URI(url), HttpMethod.POST, entity, FirebaseAuthResponse.class);

            authResponse = response.getBody();

            // log.info("authResponse: {}", ObjectUtils.toJson(authResponse));
        } catch (Exception e) {
            log.warn("Exception, msg: {}", e.getMessage());
            log.warn(ObjectUtils.toJson(response));
        }

        return authResponse;
    }

    public FirebaseAuthResponse signIn(String email, String password) {
        return signIn(email, password, true);
    }
}
