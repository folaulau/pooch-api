package com.pooch.api.elastic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.pooch.api.entity.s3file.S3FileDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerRepository;
import com.pooch.api.entity.groomer.calendar.day.CalendarDay;
import com.pooch.api.entity.groomer.calendar.day.CalendarDayDAO;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;
import com.pooch.api.entity.groomer.review.ReviewDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataLoadServiceImp implements DataLoadService {

  @Autowired
  private GroomerESRepository groomerESRepository;

  @Autowired
  private GroomerRepository groomerRepository;

  @Autowired
  private CareServiceRepository careServiceRepository;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Autowired
  private S3FileDAO s3FileDAO;

  @Autowired
  private ReviewDAO reviewDAO;


  @Autowired
  private CalendarDayDAO calendarDayDAO;

  @Override
  public ApiDefaultResponseDTO loadGroomers() {

    try {
      groomerESRepository.deleteAll();
    } catch (Exception e) {
    }

    int pageNumber = 0;
    int pageSize = 50;

    Pageable page = null;
    Page<Groomer> result = null;

    List<Groomer> groomers = null;
    List<GroomerES> esGroomers = null;

    do {

      page = PageRequest.of(pageNumber, pageSize);

      result = groomerRepository.findAll(page);

      if (result.hasContent()) {

        groomers = result.getContent();

        esGroomers = new ArrayList<>();

        for (Groomer groomer : groomers) {
          GroomerES groomerES = entityDTOMapper.mapGroomerEntityToGroomerES(groomer);
          // groomerES.populateGeoPoints();
          try {
            Optional<Set<CareService>> optCareServices =
                careServiceRepository.findByGroomerId(groomerES.getId());
            if (optCareServices.isPresent()) {
              groomerES.setCareServices(
                  entityDTOMapper.mapCareServicesToCareServiceESs(optCareServices.get()));
            }
          } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
          }

          groomer.setRating(reviewDAO.getRatingByGroomerId(groomer.getId()));

          s3FileDAO.getGroomerProfileImage(groomer.getId()).ifPresent(profileImage -> {
            groomerES.setProfileImageUrl(profileImage.getUrl());
          });

          List<CalendarDay> calendarDays = calendarDayDAO.getByGroomerIdAndDates(groomer.getId(),
              LocalDate.now(), LocalDate.now().plusMonths(6));

          try {
            groomerES.setCalendar(entityDTOMapper.mapCalendarDaysToEsCalendar(calendarDays));
          } catch (Exception e) {
            // TODO: handle exception
          }

          esGroomers.add(groomerES);
        }

        groomerESRepository.saveAll(esGroomers);

      }

      pageNumber++;

    } while (!result.isLast());

    log.info("Groomers have been loaded into Elasticsearch!");

    return new ApiDefaultResponseDTO("Groomers have been loaded into Elasticsearch!");
  }

  @Override
  public void reloadGroomer(Groomer groomer) {
    reloadGroomer(groomer, 0);
  }

  @Override
  public void reloadGroomer(Groomer groomer, double rating) {
    // TODO Auto-generated method stub

  }

}
