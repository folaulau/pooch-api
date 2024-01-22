package com.pooch.api.library.aws.secretsmanager;

import java.io.IOException;
import java.io.Serializable;

import com.pooch.api.utils.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Data
public class MailChimpSecrets implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            apiKey;

    public static MailChimpSecrets fromJson(String json) {

        try {
            return ObjectUtils.getObjectMapper().readValue(json, MailChimpSecrets.class);
        } catch (IOException e) {
            log.error("SecretManager to Json exception", e);
            return null;
        }
    }

}
