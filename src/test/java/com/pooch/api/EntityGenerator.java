package com.pooch.api;

import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.entity.petsitter.PetSitter;
import com.pooch.api.utils.RandomGeneratorUtils;

public final class EntityGenerator {

    public static PetSitter getPetSitter() {

        PetSitter petSitter = new PetSitter();
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

    public static PetParent getPetParent() {

        PetParent petParent = new PetParent();
        String firstName = RandomGeneratorUtils.getRandomFirstname();
        petParent.setFirstName(firstName);
        String lastName = RandomGeneratorUtils.getRandomLastname();
        petParent.setLastName(lastName);
        petParent.setEmail((firstName + "" + lastName).toLowerCase() + "@gmail.com");
        petParent.setEmailVerified(false);
        petParent.setPhoneNumber(RandomGeneratorUtils.getLongWithin(3101000000L, 3109999999L));
        petParent.setPhoneNumberVerified(false);

        return petParent;
    }
}
