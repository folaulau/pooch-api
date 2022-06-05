package com.pooch.api.entity.groomer.calendar.day;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.groomer.Groomer;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalendarDayServiceImp implements CalendarDayService {

  @Autowired
  private CalendarDayDAO calendarDayDAO;

  @Override
  public void addBookingToCalendar(Booking booking) {
    Groomer groomer = booking.getGroomer();

  }
}
