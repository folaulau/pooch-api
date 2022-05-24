package com.pooch.api.elastic.repo;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;
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
@Document(indexName = "groomer")
public class GroomerES implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long id;

  @Field
  private String uuid;

  @Field
  private String firstName;

  @Field
  private String lastName;

  @Field
  private String businessName;

  @Field
  private String email;

  @Field
  private Boolean emailVerified;

  @Field
  private boolean emailTemp;

  @Field
  private Long phoneNumber;

  @Field
  private Boolean phoneNumberVerified;

  @Field
  private Double rating;

  @Field
  private Boolean offeredPickUp;

  /**
   * calculated field based on search location<br>
   * Show parent how much it costs to pick up pooch based on distance between his location and
   * groomer
   */
  @Transient
  private Double pickUpCost;

  @Field
  private Boolean offeredDropOff;

  /**
   * calculated field based on search location<br>
   * Show parent how much it costs to drop off pooch based on distance between his location and
   * groomer
   */
  @Transient
  private Double dropOffCost;

  @Field
  private Double chargePerMile;

  @Field
  private Long numberOfOccupancy;

  @Field
  private String description;

  @Field
  private Boolean instantBooking;

  @Field
  private GroomerSignUpStatus signUpStatus;

  @Field
  private GroomerStatus status;

//  /**
//   * Time Groomer's shop opens
//   */
//  private LocalTime openTime;
//
//  /**
//   * Time Groomer's shop closes
//   */
//  private LocalTime closeTime;
  
  private boolean stripeReady;

  /** address */

  /** address */
  @Field(type = FieldType.Nested)
  private AddressES address;

  @Field(type = FieldType.Nested)
  private List<CareServiceES> careServices;

  @Field(type = FieldType.Nested)
  private List<CalendarES> calendar;

  @Field
  private boolean deleted;

  public void setAddress(AddressES address) {
    if (address == null) {
      return;
    }

    this.address = address;

    this.address.populateGeoPoint();
  }

  // public void populateGeoPoints() {
  // if (addresses == null || addresses.size() == 0) {
  // return;
  // }
  //
  // addresses.forEach(address -> {
  // address.populateGeoPoint();
  // });
  // }
  // public void addAddress(AddressES address) {
  // if (this.addresses == null) {
  // this.addresses = new ArrayList<>();
  // }
  // this.addresses.add(address);
  // }
  //
  // public void filterOutUnreachableLocations(GeoPoint searchLocation, int radius) {
  // if (this.addresses != null) {
  // this.addresses = this.addresses.stream().filter(address -> {
  //
  // Double distanceFromSearch = address.calculateDistanceFromSearch(searchLocation);
  //
  // if (distanceFromSearch == null || distanceFromSearch < 0 || distanceFromSearch < radius) {
  // return true;
  // }
  //
  // return false;
  // }).collect(Collectors.toList());
  // }
  // }


  /**
   * Generate calculated field's values.<br>
   * 1. distance from search<br>
   * 2. dropOff cost and pickup cost<br>
   */
  public void generateCalculatedValues(GeoPoint searchLocation, int radius) {
    Double distanceFromSearch = 0D;

    if (this.address != null) {

      distanceFromSearch = this.address.calculateDistanceFromSearch(searchLocation);

    }

    if (this.offeredDropOff != null && this.offeredDropOff) {

      this.dropOffCost = MathUtils.getTwoDecimalPlaces(distanceFromSearch * this.chargePerMile);

    }

    if (this.offeredPickUp != null && this.offeredPickUp) {

      this.dropOffCost = MathUtils.getTwoDecimalPlaces(distanceFromSearch * this.chargePerMile);
      this.pickUpCost = MathUtils.getTwoDecimalPlaces(distanceFromSearch * this.chargePerMile);

    }

  }

}
