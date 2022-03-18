package com.pooch.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterRepository;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class DatabaseTests extends IntegrationTestConfiguration {

    @Autowired
    private PetSitterRepository petSitterRepository;

    @Test
    void test_saveSitter() {

        PetSitter sitter = new PetSitter();

        sitter.setEmail(RandomGeneratorUtils.getRandomEmail());

        sitter = petSitterRepository.saveAndFlush(sitter);

        log.info("sitter={}", ObjectUtils.toJson(sitter));
    }

}
