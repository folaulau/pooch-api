package com.pooch.api.library.aws.secretsmanager;

import java.io.IOException;
import java.io.Serializable;

import com.pooch.api.utils.ObjectUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
@Data
public class StripeSecrets implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            publishableKey;

    private String            secretKey;

    private String            productId;

    private String            webhookSubscriptionSigningSecret;

    public static StripeSecrets fromJson(String json) {

        try {
            return ObjectUtils.getObjectMapper().readValue(json, StripeSecrets.class);
        } catch (IOException e) {
            log.error("SecretManager to Json exception", e);
            return null;
        }
    }

}
