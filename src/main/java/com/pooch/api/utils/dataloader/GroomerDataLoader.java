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
import com.pooch.api.entity.s3file.FileType;
import com.pooch.api.entity.s3file.S3File;
import com.pooch.api.entity.s3file.S3FileDAO;
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

  @Autowired
  private S3FileDAO s3FileDAO;

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

        /**
         * contracts
         */

        /***
         * { "id": 4, "uuid": "s3file-1653595589645-bd9bbe7c-4d16-4844-8f86-d34c3e5bbca5",
         * "fileName": "groomer-test-contract.pdf", "url":
         * "https://pooch-api-local.s3.us-west-2.amazonaws.com/public/contracts/groomer/30/eaa0b77c-6bf2-4f1d-87e5-1f4a4a79de62_groomer-test-contract.pdf",
         * "isPublic": false, "deleted": false, "createdAt": "2022-05-26T20:06:29.647952",
         * "updatedAt": "2022-05-26T20:06:29.6484" }
         */

        S3File contract = new S3File();
        contract.setFileName("groomer-test-contract.pdf");
        contract.setUrl(
            "https://pooch-api-local.s3.us-west-2.amazonaws.com/public/contracts/groomer/30/eaa0b77c-6bf2-4f1d-87e5-1f4a4a79de62_groomer-test-contract.pdf");
        contract.setFileType(FileType.Contract_Attachment);
        contract.setS3key(
            "public/contracts/groomer/30/eaa0b77c-6bf2-4f1d-87e5-1f4a4a79de62_groomer-test-contract");
        contract.setIsPublic(true);
        contract.setGroomer(groomer);


        /**
         * 
         * 
         * { "id": 5, "uuid": "s3file-1653596490762-cce1b3fe-0c54-42d9-9c8a-49c18c0e6728",
         * "fileName": "female-dog-groomer.jpeg", "url":
         * "https://pooch-api-local.s3.us-west-2.amazonaws.com/public/profile_images/groomer/30/a9d942b0-90f5-4684-8052-3fef6e51082d_female-dog-groomer.jpeg",
         * "isPublic": true, "deleted": false, "createdAt": "2022-05-26T20:21:30.772875",
         * "updatedAt": "2022-05-26T20:21:30.773069" }, { "id": 6, "uuid":
         * "s3file-1653596490806-d05ba37d-739b-404b-8b69-9b317a1f1cc9", "fileName":
         * "male-dog-groomer.jpeg", "url":
         * "https://pooch-api-local.s3.us-west-2.amazonaws.com/public/profile_images/groomer/30/2093eaef-df0e-4d99-96f9-b2720db207b0_male-dog-groomer.jpeg",
         * "isPublic": true, "deleted": false, "createdAt": "2022-05-26T20:21:30.806987",
         * "updatedAt": "2022-05-26T20:21:30.807055" }
         */



        S3File femaleProfileImage = new S3File();
        femaleProfileImage.setFileName("female-dog-groomer.jpeg");
        femaleProfileImage.setUrl(
            "https://pooch-api-local.s3.us-west-2.amazonaws.com/public/profile_images/groomer/30/a9d942b0-90f5-4684-8052-3fef6e51082d_female-dog-groomer.jpeg");
        femaleProfileImage.setFileType(FileType.Profile_Image);
        femaleProfileImage.setIsPublic(true);
        femaleProfileImage.setS3key(
            "public/profile_images/groomer/30/a9d942b0-90f5-4684-8052-3fef6e51082d_female-dog-groomer");
        femaleProfileImage.setGroomer(savedGroomer);

        S3File maleProfileImage = new S3File();
        maleProfileImage.setFileName("male-dog-groomer.jpeg");
        maleProfileImage.setUrl(
            "https://pooch-api-local.s3.us-west-2.amazonaws.com/public/profile_images/groomer/30/2093eaef-df0e-4d99-96f9-b2720db207b0_male-dog-groomer.jpeg");
        maleProfileImage.setFileType(FileType.Profile_Image);
        maleProfileImage.setIsPublic(true);
        maleProfileImage.setS3key(
            "public/profile_images/groomer/30/2093eaef-df0e-4d99-96f9-b2720db207b0_male-dog-groomer");
        maleProfileImage.setGroomer(savedGroomer);


        S3File profileImage = Arrays.asList(femaleProfileImage, maleProfileImage)
            .get(RandomGeneratorUtils.getIntegerWithin(0, 1));

        profileImage.setMainProfileImage(true);

        try {

          s3FileDAO.save(Arrays.asList(contract, profileImage));
        } catch (Exception e) {
          log.warn("Loading Groomer S3Files Exception, msg={}", e.getLocalizedMessage());
        }

        savedGroomer = groomerDAO.save(savedGroomer);

      }
    } catch (Exception e) {
      log.warn("Loading Groomer Exception, msg={}", e.getLocalizedMessage());
    }

  }

}
