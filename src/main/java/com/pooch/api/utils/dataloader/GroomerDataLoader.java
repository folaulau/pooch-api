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
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;
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
    private GroomerDAO                 groomerDAO;

    @Autowired
    private GroomerRepository          groomerRepository;

    @Autowired
    private CareServiceRepository      careServiceRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        long lastGroomerId = 10;

        Optional<Groomer> optGroomer = groomerRepository.findById(lastGroomerId);

        if (optGroomer.isPresent()) {
            log.info("Groomer seed data has been loaded already!");
            return;
        }

        for (int i = 0; i < lastGroomerId; i++) {
            Groomer groomer = generatorService.getDBGroomer();
            groomer.setId((long) (i + 1));
            groomer.setAddresses(null);
            Address address = generatorService.getAddress();
            address.setId((long) (i + 1));

            Groomer savedGroomer = groomerDAO.save(groomer);

            List<String> services = Arrays.asList("Dog Daycare", "Grooming", "Overnight", "Nail Clipping");

            int j = 1;
            if (i != 0) {
                j = i * 4;
            }

            List<CareService> careServices = new ArrayList<>();

            for (int k = 0; k < services.size(); j++, k++) {
                String name = services.get(k);

                CareService service = new CareService();
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

        }

    }

}
