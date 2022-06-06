package com.pooch.api.entity.groomer.calendar.day;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.groomer.Groomer;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalendarDayServiceImp implements CalendarDayService {

  @Autowired
  private CalendarDayDAO calendarDayDAO;

  @Transactional
  @Lock(LockModeType.PESSIMISTIC_READ)
  @Override
  public void addBookingToCalendar(Booking booking) {
    Groomer groomer = booking.getGroomer();

    // update each day with 1 count

    LocalDateTime startDateTime = booking.getStartDateTime();
    LocalDateTime endDateTime = booking.getEndDateTime();

    LocalTime openTime = groomer.getOpenTime();
    LocalTime closeTime = groomer.getCloseTime();

    List<CalendarDay> days = new ArrayList<>();

    LocalDateTime countFromStartDateTime = startDateTime;

    do {

      LocalDate date = countFromStartDateTime.toLocalDate();

      CalendarDay calendarDay =
          calendarDayDAO.getByGroomerIdAndDate(groomer.getId(), date).orElse(new CalendarDay());

      calendarDay.setGroomer(groomer);
      calendarDay.setDate(date);
      calendarDay.generateFill(groomer.getNumberOfOccupancy());
      calendarDay.setOperational(groomer.checkOperationByDay(date));
      calendarDay.addBookingCount();

      days.add(calendarDay);

      countFromStartDateTime = countFromStartDateTime.plusDays(1);

    } while (countFromStartDateTime.isBefore(endDateTime));


    if (days.size() > 0) {
      calendarDayDAO.save(days);
    }
  }
}
