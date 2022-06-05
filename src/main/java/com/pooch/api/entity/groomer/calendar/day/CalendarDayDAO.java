package com.pooch.api.entity.groomer.calendar.day;

import java.util.List;

public interface CalendarDayDAO {

  List<CalendarDay> save(List<CalendarDay> days);

  CalendarDay save(CalendarDay day);
}
