package com.pooch.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerRepository;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class DatabaseTests extends IntegrationTestConfiguration {

    @Autowired
    private GroomerRepository petSitterRepository;

    @Test
    void test_saveSitter() {

        Groomer sitter = new Groomer();

        sitter.setEmail(RandomGeneratorUtils.getRandomEmail());

        sitter = petSitterRepository.saveAndFlush(sitter);

        log.info("sitter={}", ObjectUtils.toJson(sitter));
    }

}
