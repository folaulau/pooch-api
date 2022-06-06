package com.pooch.api.entity.groomer.calendar.day;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CalendarDayDAOImp implements CalendarDayDAO {

  @Autowired
  private CalendarDayRepository calendarDayRepository;


  @Autowired
  private JdbcTemplate jdbcTemplate;

  
  @Transactional
  @Override
  public List<CalendarDay> save(List<CalendarDay> days) {
    return calendarDayRepository.saveAllAndFlush(days);
  }


  @Override
  public CalendarDay save(CalendarDay day) {
    return calendarDayRepository.saveAndFlush(day);
  }


  @Override
  public Optional<CalendarDay> getByGroomerIdAndDate(long groomerId, LocalDate date) {
    return calendarDayRepository.findByGroomerIdAndDate(groomerId, date);
  }



}
