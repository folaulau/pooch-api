package com.pooch.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterRepository;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Disabled
@Slf4j
@SpringBootTest
class FirebaseTests {

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
