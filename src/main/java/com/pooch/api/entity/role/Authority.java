package com.pooch.api.entity.role;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Authority {

    pet_parent,

    pet_sitter,

    sales,

    engineer,

    admin;

    public static List<String> getAllAuths() {
        return Arrays.asList(Authority.values()).stream().map(auth -> auth.name()).collect(Collectors.toList());
    }

}
