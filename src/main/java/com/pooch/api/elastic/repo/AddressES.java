package com.pooch.api.elastic.repo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.utils.MathUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class AddressES implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            uuid;

    private String            street;

    private String            street2;

    private String            city;

    private String            state;

    private String            zipcode;

    private String            country;

    private Double            longitude;

    private Double            latitude;

    @GeoPointField
    private GeoPoint          location;

    /**
     * calculated field based on search location
     */
    @Transient
    private Double            distanceFromSearch;

    private String            timezone;

    public void populateGeoPoint() {
        if (longitude == null || latitude == null) {
            return;
        }
        this.location = new GeoPoint(latitude, longitude);
    }

    public Double calculateDistanceFromSearch(GeoPoint searchLocation) {
        distanceFromSearch = MathUtils.distance(searchLocation.getLat(), latitude, searchLocation.getLon(), longitude);

        return distanceFromSearch;
    }
}
