package com.pooch.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public final class Money{

    public static Long convertDollarsToCents(Double dollars) {
        if (dollars == null) {
            return null;
        }
        return new BigDecimal(dollars).multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP).longValue();
    }

    public static Double convertCentsToDollars(Long cents) {
        if (cents == null) {
            return null;
        }
        return new BigDecimal(cents).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
