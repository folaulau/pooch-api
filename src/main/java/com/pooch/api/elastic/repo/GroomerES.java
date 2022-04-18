package com.pooch.api.elastic.repo;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@Document(indexName = "groomer")
public class GroomerES implements Serializable {

    private static final long   serialVersionUID = 1L;

    @Id
    private Long                id;

    @Field
    private String              uuid;

    @Field
    private String              firstName;

    @Field
    private String              lastName;

    @Field
    private String              businessName;

    @Field
    private String              email;

    @Field
    private Boolean             emailVerified;

    @Field
    private boolean             emailTemp;

    @Field
    private Long                phoneNumber;

    @Field
    private Boolean             phoneNumberVerified;

    @Field
    private Double              rating;

    @Field
    private Boolean             offeredPickUp;

    @Field
    private Boolean             offeredDropOff;

    @Field
    private Double              chargePerMile;

    @Field
    private Long                numberOfOccupancy;

    @Field
    private String              description;

    @Field
    private Boolean             instantBooking;

    /** address */

    /** address */
    @Field(type = FieldType.Nested)
    private List<AddressES>     addresses;

    @Field(type = FieldType.Nested)
    private List<CareServiceES> careServices;

    @Field(type = FieldType.Nested)
    private List<CalendarES>    calendar;

    @Field
    private boolean             deleted;

    public void populateGeoPoints() {
        if (addresses == null || addresses.size() == 0) {
            return;
        }

        addresses.forEach(address -> {
            address.populateGeoPoint();
        });
    }

    public void addAddress(AddressES address) {
        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }
        this.addresses.add(address);
    }

    public void filterOutUnreachableLocations(GeoPoint searchLocation, int radius) {
        if (this.addresses != null) {
            this.addresses = this.addresses.stream().filter(address -> {

                Double distanceFromSearch = address.calculateDistanceFromSearch(searchLocation);

                if (distanceFromSearch == null || distanceFromSearch < 0 || distanceFromSearch < radius) {
                    return true;
                }

                return false;
            }).collect(Collectors.toList());
        }
    }

}
