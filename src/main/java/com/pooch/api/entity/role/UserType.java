package com.pooch.api.entity.role;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UserType {

    parent,

    groomer,
    
    groomer_agent,

    sales,
    
    support,

    engineer,

    admin;

    public static List<String> getAllAuths() {
        return Arrays.asList(UserType.values()).stream().map(auth -> auth.name()).collect(Collectors.toList());
    }

}
