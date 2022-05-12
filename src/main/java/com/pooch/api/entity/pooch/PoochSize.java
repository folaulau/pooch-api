package com.pooch.api.entity.pooch;

import java.util.Arrays;
import java.util.List;

public interface PoochSize {

    String       small  = "small";
    String       medium = "medium";
    String       large  = "large";

    List<String> sizes  = Arrays.asList(small, medium, large);

    static boolean isValidSize(String value) {
        return value != null && sizes.contains(value.toLowerCase());
    }
}
