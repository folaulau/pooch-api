package com.pooch.api.xapikey;

public interface XApiKeyService {

    boolean validate(String xApiKey);
    
    boolean validateForPoochAppMobile(String xApiKey);

    boolean validateForUtility(String xApiKey);
}
