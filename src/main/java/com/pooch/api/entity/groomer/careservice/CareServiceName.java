package com.pooch.api.entity.groomer.careservice;

import java.util.Arrays;
import java.util.List;

public interface CareServiceName {

    List<String> careServiceNames = Arrays.asList("Dog Daycare", "Grooming", "Overnight", "Nail Clipping","Pick up/ Drop off");

    public static boolean isValidCareServiceName(String name) {
        return name != null && !name.trim().isEmpty() && !name.trim().isBlank() && careServiceNames.contains(name);
    }
}
