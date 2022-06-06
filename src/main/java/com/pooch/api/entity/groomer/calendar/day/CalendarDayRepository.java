package com.pooch.api.entity.groomer.calendar.day;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long> {

  Optional<CalendarDay> findByGroomerIdAndDate(Long groomerId, LocalDate date);
}
