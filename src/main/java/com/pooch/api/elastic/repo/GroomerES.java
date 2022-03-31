package com.pooch.api.elastic.repo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

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

  private static final long serialVersionUID = 1L;

  @Id private Long id;

  @Field private String uuid;

  @Field private String firstName;

  @Field private String lastName;

  @Field private String businessName;

  @Field private String email;

  @Field private Boolean emailVerified;

  @Field private boolean emailTemp;

  @Field private Long phoneNumber;

  @Field private Boolean phoneNumberVerified;

  @Field private Double rating;

  @Field private Boolean offeredPickUp;

  @Field private Boolean offeredDropOff;

  @Field private Double chargePerMile;

  @Field private Long numberOfOcupancy;

  @Field private String description;

  @Field private Boolean instantBooking;

  /** address */

  /** address */
  @Field(type = FieldType.Nested)
  private List<AddressES> addresses;

  @Field(type = FieldType.Nested)
  private List<CareServiceES> careServices;

  @Field private boolean deleted;

  @Field(format = DateFormat.basic_date_time)
  private LocalDateTime createdAt;

  @Field(format = DateFormat.basic_date_time)
  private LocalDateTime updatedAt;

  public void populateGeoPoints() {
    if (addresses == null || addresses.size() == 0) {
      return;
    }

    addresses.forEach(
        address -> {
          address.populateGeoPoint();
        });
  }
}
