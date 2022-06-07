package com.pooch.api.elastic.repo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Column;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Groomer Calendars
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class CalendarDayES implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String uuid;

  @Field(type = FieldType.Date, format = {DateFormat.basic_date, DateFormat.basic_time})
  private Date date;

  private Boolean operational;

  private Boolean filled;

  private Integer numberOfBookings;
}
