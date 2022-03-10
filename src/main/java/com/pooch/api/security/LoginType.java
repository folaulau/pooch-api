package com.pooch.api.security;

import java.util.Arrays;

public enum LoginType {

    USER,
    ADMIN,
    NONE;

    public static LoginType getLoginType(String str) {
        if (null == str || str.length() == 0) {
            return NONE;
        }

        return Arrays.asList(LoginType.values()).stream().filter(loginType -> loginType.name().toLowerCase().equals(str.toLowerCase())).findFirst().orElse(NONE);
    }
}
