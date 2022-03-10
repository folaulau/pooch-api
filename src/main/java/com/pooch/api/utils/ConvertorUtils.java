package com.pooch.api.utils;

import java.math.BigDecimal;

public final class ConvertorUtils {

    public static BigDecimal convertDollarsToCents(double dollars) {
        return BigDecimal.valueOf(dollars * 100);
    }

    public static BigDecimal convertCentsToDollars(long cents) {
        return BigDecimal.valueOf(cents / 100);
    }

}
