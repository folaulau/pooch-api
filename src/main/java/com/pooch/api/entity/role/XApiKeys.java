package com.pooch.api.entity.role;

import java.util.Arrays;

public interface XApiKeys {

    String POOCHAPP_MOBILE = "2c6805f9-fa9f-42be-810e-6ad32900ad3c";
    String POOCHFOLIO_WEB  = "f9609c0c-6e70-42c3-afaf-10f64bc02b21";
    String UTILITY_API     = "f67a6af8-828f-475b-ab8f-70a555ece9e4";

    public static boolean isValid(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return Arrays.asList(POOCHAPP_MOBILE, POOCHFOLIO_WEB).contains(key);
    }

    public static boolean isUtilityValid(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return UTILITY_API.equalsIgnoreCase(key);
    }
}
