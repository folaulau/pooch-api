package com.pooch.api.entity.pooch;

import java.util.Arrays;
import java.util.List;

public interface PoochSize {

  // 1-20 LB
  String small = "small";
  // 21-40 LB
  String medium = "medium";
  // 41+ LB
  String large = "large";

  List<String> sizes = Arrays.asList(small, medium, large);

  static boolean isValidSize(String value) {
    return value != null && sizes.contains(value.toLowerCase());
  }

  static String getSizeByWeight(Double value) {
    if (value == null || value <= 0) {
      return null;
    }
    if (value > 0 && value <= 20) {
      return small;
    } else if (value > 20 && value <= 40) {
      return medium;
    }
    return large;
  }
}
