package com.pooch.api.library.aws.secretsmanager;

import com.pooch.api.utils.ObjectUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;

@NoArgsConstructor
@Slf4j
@Data
public class ElasticsearchSecrets implements Serializable {

  /** */
  private static final long serialVersionUID = 1L;

  private String username;
  private String password;
  private String host;
  private String httpType;
  private int port;

  public static ElasticsearchSecrets fromJson(String json) {

    try {
      return ObjectUtils.getObjectMapper().readValue(json, ElasticsearchSecrets.class);
    } catch (IOException e) {
      log.error("SecretManager to Json exception", e);
      return null;
    }
  }
}
