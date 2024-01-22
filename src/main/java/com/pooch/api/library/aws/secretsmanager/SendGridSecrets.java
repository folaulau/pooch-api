package com.pooch.api.library.aws.secretsmanager;

import java.io.IOException;
import java.io.Serializable;

import com.pooch.api.utils.ObjectUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SendGridSecrets implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String apiKey;

  public static SendGridSecrets fromJson(String json) {

    try {
      return ObjectUtils.getObjectMapper().readValue(json, SendGridSecrets.class);
    } catch (IOException e) {
      log.error("SecretManager to Json exception", e);
      return null;
    }
  }

}
