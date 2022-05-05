package utils.tests;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.utils.HttpRequestInterceptor;
import com.pooch.api.utils.HttpResponseErrorHandler;
import com.pooch.api.utils.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Data
public class TestApiService {

    private RestTemplate   restTemplate;

    private TestApiSession session;

    public TestApiService(TestApiSession session) {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(100000);
        requestFactory.setReadTimeout(100000);
        restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getInterceptors().add(new HttpRequestInterceptor());
        restTemplate.setErrorHandler(new HttpResponseErrorHandler());

        this.session = session;
    }

    public FirebaseAuthResponse signUpWithFirebase(String email, String password, boolean returnSecureToken) {
        String firebaseWebApiKey = session.getFirebaseWebAuthKey();

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
        return signUpWithFirebase(email, password, true);
    }

    public FirebaseAuthResponse signInWithFirebase(String email, String password, boolean returnSecureToken) {
        log.info("signIn()");
        String firebaseWebApiKey = session.getFirebaseWebAuthKey();
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

}
