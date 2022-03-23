package com.pooch.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pooch.api.entity.pet.Breed;
import com.pooch.api.entity.pet.FoodSchedule;
import com.pooch.api.entity.pet.Pet;
import com.pooch.api.entity.pet.PetRepository;
import com.pooch.api.entity.pet.vaccine.Vaccine;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.entity.petparent.PetParentRepository;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.entity.petsitter.PetSitterRepository;
import com.pooch.api.utils.RandomGeneratorUtils;

@Repository
public class TestEntityGeneratorService {

    @Autowired
    private PetSitterRepository petSitterRepository;

    @Autowired
    private PetParentRepository petParentRepository;

    @Autowired
    private PetRepository       petRepository;

    public PetSitter getDBPetSitter() {
        PetSitter petSitter = getPetSitter();
        return petSitterRepository.saveAndFlush(petSitter);
    }

    public PetSitter getPetSitter() {

        PetSitter petSitter = new PetSitter();
        petSitter.setUuid("pet-sitter-" + UUID.randomUUID().toString());
        String firstName = RandomGeneratorUtils.getRandomFirstname();
        petSitter.setFirstName(firstName);
        String lastName = RandomGeneratorUtils.getRandomLastname();
        petSitter.setLastName(lastName);
        petSitter.setEmail((firstName + "" + lastName).toLowerCase() + "@gmail.com");
        petSitter.setEmailVerified(false);

        petSitter.setNumberOfOcupancy(RandomGeneratorUtils.getLongWithin(2L, 100L));
        petSitter.setChargePerMile(RandomGeneratorUtils.getDoubleWithin(1D, 3D));
        petSitter.setOfferedDropOff(true);
        petSitter.setOfferedPickUp(true);

        petSitter.setDescription("Test description");

        petSitter.setPhoneNumber(RandomGeneratorUtils.getLongWithin(3101000000L, 3109999999L));
        petSitter.setPhoneNumberVerified(false);

        petSitter.setRating(RandomGeneratorUtils.getIntegerWithin(1, 5));

        return petSitter;
    }

    public PetParent getDBPetParent() {
        PetParent petParent = getPetParent();
        return petParentRepository.saveAndFlush(petParent);
    }

    public PetParent getPetParent() {

        PetParent petParent = new PetParent();
        petParent.setUuid("pet-parent-" + UUID.randomUUID().toString());
        String firstName = RandomGeneratorUtils.getRandomFirstname();
        String lastName = RandomGeneratorUtils.getRandomLastname();
        petParent.setFullName(firstName + " " + lastName);
        petParent.setEmail((firstName + "" + lastName).toLowerCase() + "@gmail.com");
        petParent.setEmailVerified(false);
        petParent.setPhoneNumber(RandomGeneratorUtils.getLongWithin(3101000000L, 3109999999L));
        petParent.setPhoneNumberVerified(false);
        petParent.setRating(RandomGeneratorUtils.getIntegerWithin(1, 5));

        return petParent;
    }

    public Pet getDBPet(PetParent petParent) {
        Pet pet = getPet(petParent);
        return petRepository.saveAndFlush(pet);
    }

    public Pet getPet() {
        return getPet(null);
    }

    public Pet getPet(PetParent petParent) {

        Pet pet = new Pet();
        pet.setBreed(Breed.Bulldog);
        pet.setPetParent(petParent);
        pet.setDob(LocalDate.now().minusMonths(RandomGeneratorUtils.getLongWithin(6, 60)));
        pet.addFoodSchedule(FoodSchedule.Morning);
        pet.addFoodSchedule(FoodSchedule.Night);
        pet.addVaccine(new Vaccine("vaccine", LocalDateTime.now().plusDays(24)));
        return pet;
    }
}
