package com.pooch.api.entity.groomer.calendar.day;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarDayDAO {

  List<CalendarDay> save(List<CalendarDay> days);

  CalendarDay save(CalendarDay day);

  Optional<CalendarDay> getByGroomerIdAndDate(long groomerId, LocalDate date);
}
