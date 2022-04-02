package com.pooch.api.entity.groomer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.pooch.api.utils.MathUtils;

class GroomerMockTests {

    @Test
    void checkForSearchLocationFilters() {
        /**
         * 1043 Franklin St, Santa Monica, CA 90403<br>
         * lat: 34.043148, long: -118.4750169<br>
         * 
         */

        /**
         * 1116 Stanford St, Santa Monica, CA 90403<br>
         * lat: 34.0400821, -118.475029<br>
         */

        double distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0400821, -118.4750169, -118.475029);
        System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

        /**
         * 3408 Pearl St, Santa Monica, CA 90405<br>
         * lat: 34.0251161, -118.4517642<br>
         */

        distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0251161, -118.4750169, -118.4517642);
        System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

        /**
         * 12107 Palms Blvd, Los Angeles, CA 90066<br>
         * lat: 34.0124107, long: -118.4355353<br>
         */

        distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0124107, -118.4750169, -118.4355353);
        System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    }

}
