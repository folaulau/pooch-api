package com.pooch.api.entity.groomer.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BookingStatisticsDAOImp implements BookingStatisticsDAO {

  @Autowired
  BookingStatisticsRepository bookingStatisticsRepository;

  @Override
  public BookingStatistics save(BookingStatistics bookingStatistics) {
    return bookingStatisticsRepository.saveAndFlush(bookingStatistics);
  }

}
