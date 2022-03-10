package com.pooch.api.aws.secretsmanager;

import java.io.IOException;
import java.io.Serializable;

import com.pooch.api.utils.ObjectUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class DatabaseSecrets implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            username;

    private String            password;

    private String            engine;

    private String            host;

    private String            dbInstanceIdentifier;

    public static DatabaseSecrets fromJson(String json) {

        try {
            return ObjectUtils.getObjectMapper().readValue(json, DatabaseSecrets.class);
        } catch (IOException e) {
            log.error("SecretManager to Json exception", e);
            return null;
        }
    }

}
