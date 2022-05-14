package com.pooch.api.xapikey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pooch.api.exception.ApiException;
import com.pooch.api.library.aws.secretsmanager.XApiKey;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class XApiKeyServiceImp implements XApiKeyService {

    @Autowired
    @Qualifier(value = "xApiKey")
    private XApiKey xApiKey;

    @Override
    public boolean validate(String apiKey) {
        boolean result = xApiKey.isValid(apiKey);

        if (result) {
            return true;
        }

        throw new ApiException("Invalid x-api-key", "x-api-key not found, " + xApiKey);
    }

    @Override
    public boolean validateForUtility(String apiKey) {
        boolean result = xApiKey.isUtilityValid(apiKey);

        if (result) {
            return true;
        }

        throw new ApiException("Invalid x-api-key for utility", "x-api-key not found, " + xApiKey);
    }

    @Override
    public boolean validateForPoochAppMobile(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return xApiKey.getMobileXApiKey().equalsIgnoreCase(key);
    }

}
