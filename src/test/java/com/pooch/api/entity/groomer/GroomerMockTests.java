package com.pooch.api.entity.groomer;

import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import com.pooch.api.elastic.repo.AddressES;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.utils.MathUtils;
import com.pooch.api.utils.ObjectUtils;

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

    @Test
    void filterAddresses() {
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

        GroomerES groomer = new GroomerES();

        AddressES address = new AddressES();
        address.setLatitude(34.043148);
        address.setLongitude(-118.4750169);
        groomer.addAddress(address);
        /**
         * 3408 Pearl St, Santa Monica, CA 90405<br>
         * lat: 34.0251161, -118.4517642<br>
         */

        distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0251161, -118.4750169, -118.4517642);
        System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

        address = new AddressES();
        address.setLatitude(34.0251161);
        address.setLongitude(-118.4517642);
        groomer.addAddress(address);

        /**
         * 12107 Palms Blvd, Los Angeles, CA 90066<br>
         * lat: 34.0124107, long: -118.4355353<br>
         */

        distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0124107, -118.4750169, -118.4355353);
        System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

        address = new AddressES();
        address.setLatitude(34.0124107);
        address.setLongitude(-118.4355353);
        groomer.addAddress(address);

        groomer.filterOutUnreachableLocations(new GeoPoint(34.043148, -118.4750169), 3);
        
        System.out.println("groomer: "+ObjectUtils.toJson(groomer));
    }

}
