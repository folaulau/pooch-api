package com.pooch.api.dto;

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
public class BookingCareServiceDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long bookingCareServiceId;

  private Long id;

  private String uuid;

  private Double price;

  private String size;

}
