package com.pooch.api.elastic.repo;

import java.io.Serializable;
import java.time.LocalDate;

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
public class CalendarES implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate         date;

    /**
     * number of occupancy per day
     */
    private long              numberOfOccupancy;

    /**
     * number of opening per day
     */
    private long              numberOfOpenings;

    /**
     * number of booking(already booked) per day
     */
    private long              numberOfBookings;
}
