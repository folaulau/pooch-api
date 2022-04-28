package com.pooch.api.entity.groomer.careservice.type;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class CareServiceTypeDataLoader implements ApplicationRunner {

    @Autowired
    private GroomerServiceCategoryRepository groomerServiceCategoryRepository;

    /**
     * List<String> careServiceNames = Arrays.asList("Dog Daycare", "Grooming", "Overnight", "Nail Clipping","Pick up/
     * Drop off");
     * 
     */

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // @formatter:off
 
      List<String> groomerCatogories = List.of("Dog Daycare",
              "Grooming",
              "Overnight",
              "Nail Clipping",
              "Pick up/Drop off",
              "Dog Bath Double Coat",
              "Dog Bath Soft Coat",
              "Dog Bath Wire Coat",
              "Dog Bath Short Coat",
              "Haircut",
              "Touch Ups",
              "Special Handling",
              "De-shedding",
              "De-skunkin",
              "Flea Bath",
              "Cut and Style",
              "Deep Cleaning Shampoo",
              "Blow dry",
              "Wash and Blow Dry",
              "Scented Spritz",
              "Gland Expression",
              "Nail Trim",
              "Teeth Cleaning");
      
          // @formatter:on

      // @formatter:off
      
      List<String> kennelCatogories = List.of("Interview",
            "Private Room",
            "Deluxe Suite",
            "Ultra Suite",
            "King Luxury Suite",
            "Luxury Suite",
            "Classic Suite",
            "Full Day",
            "Half Day");
      
      long latestRowId = kennelCatogories.size() + groomerCatogories.size();
      
//      groomerServiceCategoryRepository.findById(latestRowId).ifPresent(groomerServiceCategory -> {
//          return;
//      });
      
      long count = 1;

      for(String category: groomerCatogories) {
          groomerServiceCategoryRepository.saveAndFlush(new GroomerServiceCategory(count, category, CareServiceType.Groomer));
          count++;
      }
      
      for(String category: kennelCatogories) {
          groomerServiceCategoryRepository.saveAndFlush(new GroomerServiceCategory(count, category, CareServiceType.Kennel));
          count++;
      }

        // @formatter:on
    }

}
