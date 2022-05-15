package com.pooch.api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public interface MathUtils {

    /**
     * reference: https://www.geeksforgeeks.org/program-distance-two-points-earth/
     */
    public static Double distance(double lat1, double lat2, double lon1, double lon2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        Double c = null;

        try {
            lon1 = Math.toRadians(lon1);
            lon2 = Math.toRadians(lon2);
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            // Haversine formula
            double dlon = lon2 - lon1;
            double dlat = lat2 - lat1;
            double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

            c = 2 * Math.asin(Math.sqrt(a));
        } catch (Exception e) {
            System.out.println("Exception, msg=" + e.getLocalizedMessage());
        }

        if (c == null) {
            return null;
        }

        // Radius of earth in kilometers.
        // Use 3956 for miles
        // Use 6371 for km
        double r = 3956;

        // calculate the result
        double dist = (c * r);

        return new BigDecimal(dist).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // Get Two Decimal String
    public static BigDecimal getStrTwoDecimalPlaces(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
    }

    // Get Two Decimal Double
    public static Double getTwoDecimalPlaces(Double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static String getTwoDecimalPlacesAsString(Double value) {
        double amount = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(amount);
    }

    public static String formatDollarAmountWithTwoDecimalAsString(double amount) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(amount);
    }

    public static Float getTwoDecimalPlacesAsFloat(Double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }
}
