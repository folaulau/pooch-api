package com.pooch.api.utils.dataloader;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentRepository;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"local", "dev", "qa"})
@Component
public class ParentDataLoader implements ApplicationRunner {

  @Autowired
  private TestEntityGeneratorService generatorService;

  @Autowired
  private ParentRepository parentRepository;

  @Autowired
  private FirebaseRestClient firebaseRestClient;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    long lastParentId = 20;

    Optional<Parent> optParent = parentRepository.findById(lastParentId);

    if (optParent.isPresent()) {
      log.info("Parent seed data has been loaded already!");
      return;
    }

    try {

      Parent parent = null;
      Address address = null;

      for (int i = 0; i < lastParentId; i++) {
        parent = generatorService.getParent();
        parent.setId((long) (i + 1));

        address = generatorService.getAddress();

        parent.setAddress(address);
        log.info("parent#={}, {}", (i + 1), ObjectUtils.toJson(parent));

        parent = parentRepository.saveAndFlush(parent);

        firebaseRestClient.signUpAsync(parent.getEmail(), "Test1234!");

        log.info("done with parent#", (i + 1));

      }
    } catch (Exception e) {
      log.warn("Exception, msg={}", e.getLocalizedMessage());
    }

  }

}
