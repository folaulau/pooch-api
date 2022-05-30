package com.pooch.api.entity.booking.careservice;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.booking.pooch.BookingPooch;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.pooch.Pooch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@Table(name = DatabaseTableNames.BookingCareService)
public class BookingCareService implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "booking_care_service_id", nullable = false, updatable = false, unique = true)
  private Long bookingCareServiceId;

  @Column(name = "id")
  private Long id;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "price")
  private Double price;

  @Column(name = "size")
  private String size;
  
  @JsonIgnoreProperties(value = {"careServices"})
  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "booking_id")
  private Booking booking;


}
