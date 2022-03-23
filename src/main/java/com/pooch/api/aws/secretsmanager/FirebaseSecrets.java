package com.pooch.api.aws.secretsmanager;

import java.io.IOException;
import java.io.Serializable;

import com.pooch.api.utils.ObjectUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
@Data
public class FirebaseSecrets implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            authWebApiKey;

    public static FirebaseSecrets fromJson(String json) {

        try {
            return ObjectUtils.getObjectMapper().readValue(json, FirebaseSecrets.class);
        } catch (IOException e) {
            log.error("SecretManager to Json exception", e);
            return null;
        }
    }

}
