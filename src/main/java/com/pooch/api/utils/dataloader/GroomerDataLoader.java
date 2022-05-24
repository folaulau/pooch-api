package com.pooch.api.utils.dataloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.groomer.GroomerRepository;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"local", "dev", "qa"})
@Component
public class GroomerDataLoader implements ApplicationRunner {

  @Autowired
  private TestEntityGeneratorService generatorService;

  @Autowired
  private GroomerDAO groomerDAO;

  @Autowired
  private GroomerRepository groomerRepository;

  @Autowired
  private CareServiceRepository careServiceRepository;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    long lastGroomerId = 10;

    Optional<Groomer> optGroomer = groomerRepository.findById(lastGroomerId);

    if (optGroomer.isPresent()) {
      log.info("Groomer seed data has been loaded already!");
      return;
    }

    try {

      Groomer groomer = null;
      Address address = null;
      CareService service = null;

      for (int i = 0; i < lastGroomerId; i++) {
        groomer = generatorService.getGroomer();
        groomer.setSignUpStatus(GroomerSignUpStatus.COMPLETED);
        groomer.setStatus(GroomerStatus.ACTIVE);
        groomer.setId((long) (i + 1));
        groomer.setAddress(null);

        address = generatorService.getAddress();

        if (i == 3) {
          address = new Address();
          address.setCity("Venice");
          address.setState("CA");
          address.setZipcode("90291");
          address.setStreet("1800 Ocean Front Walk");
          address.setLatitude(33.9850469);
          address.setLongitude(-118.4694832);
        }

        if (i == 2) {
          groomer.setStripeConnectedAccountId("acct_1Kwbek2EkAQclp9I");
          groomer.setStripeChargesEnabled(true);
          groomer.setStripeDetailsSubmitted(true);
          groomer.setStripePayoutsEnabled(true);
          groomer.setStripeAcceptCardPayments("card_payments");
        }

        address.setId((long) (i + 1));
        address.setGroomer(groomer);
        groomer.setAddress(address);

        log.info("groomer#={}, {}", (i + 1), ObjectUtils.toJson(groomer));

        Groomer savedGroomer = groomerDAO.save(groomer);

        List<String> services =
            Arrays.asList("Dog Daycare", "Grooming", "Overnight", "Nail Clipping");

        int j = 1;
        if (i != 0) {
          j = i * 4;
        }

        List<CareService> careServices = new ArrayList<>();

        for (int k = 0; k < services.size(); j++, k++) {
          String name = services.get(k);

          service = new CareService();
          service.setId((long) (i + j));
          service.setName(name);
          service.setServiceSmall(true);
          service.setSmallPrice((double) RandomGeneratorUtils.getIntegerWithin(10, 40));
          service.setServiceMedium(true);
          service.setMediumPrice((double) RandomGeneratorUtils.getIntegerWithin(40, 80));
          service.setServiceLarge(true);
          service.setLargePrice((double) RandomGeneratorUtils.getIntegerWithin(80, 120));
          service.setDescription("random text");
          service.setGroomer(savedGroomer);

          careServices.add(service);
        }

        careServiceRepository.saveAll(careServices);

        log.info("done with groomer#", (i + 1));

      }
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

  }

}
