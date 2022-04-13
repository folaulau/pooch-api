package com.pooch.api.entity.groomer;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum GroomerSearchSorting {

    distance,
    rating,
    search_phrase;

    public static List<String> sortings = Arrays.asList(GroomerSearchSorting.values()).stream().map(sort -> sort.name()).toList();

    public static boolean exist(String sort) {
        if (sort == null || sort.length() == 0) {
            return false;
        }
        return sortings.contains(sort.toLowerCase());
    }
}
