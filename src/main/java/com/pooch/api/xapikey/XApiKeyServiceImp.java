package com.pooch.api.xapikey;

import org.springframework.stereotype.Service;

import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class XApiKeyServiceImp implements XApiKeyService {

    @Override
    public boolean validate(String xApiKey) {
        boolean result = XApiKeys.isValid(xApiKey);

        if (result) {
            return true;
        }

        throw new ApiException("Invalid x-api-key", "x-api-key not found, " + xApiKey);
    }

    @Override
    public boolean validateForUtility(String xApiKey) {
        boolean result = XApiKeys.isUtilityValid(xApiKey);

        if (result) {
            return true;
        }

        throw new ApiException("Invalid x-api-key for utility", "x-api-key not found, " + xApiKey);
    }

}
