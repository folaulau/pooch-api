package com.pooch.api.entity.groomer.calendar.day;

import com.pooch.api.entity.booking.Booking;

public interface CalendarDayService {

  void addBookingToCalendar(Booking booking);
}
