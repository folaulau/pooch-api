package com.pooch.api.library.firebase;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.pooch.api.library.aws.secretsmanager.FirebaseSecrets;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

public interface FirebaseRestClient {

  public FirebaseAuthResponse signUp(String email, String password, boolean returnSecureToken);

  public FirebaseAuthResponse signUp(String email, String password);

  @Async
  public void signUpAsync(String email, String password);

  public FirebaseAuthResponse signIn(String email, String password, boolean returnSecureToken);

  public FirebaseAuthResponse signIn(String email, String password);
}
