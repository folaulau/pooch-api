package com.pooch.api.library.aws.secretsmanager;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import com.pooch.api.utils.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Data
public class XApiKey implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            webXApiKey;

    private String            mobileXApiKey;

    private String            utilityXApiKey;

    public boolean isValid(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return Arrays.asList(webXApiKey, mobileXApiKey, utilityXApiKey).contains(key);
    }

    public boolean isUtilityValid(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return utilityXApiKey.equalsIgnoreCase(key);
    }

    public static XApiKey fromJson(String json) {

        try {
            return ObjectUtils.getObjectMapper().readValue(json, XApiKey.class);
        } catch (IOException e) {
            log.error("SecretManager to Json exception", e);
            return null;
        }
    }

}
