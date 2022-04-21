package com.pooch.api.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pooch.api.entity.address.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerRepository;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentRepository;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochRepository;
import com.pooch.api.entity.pooch.vaccine.Vaccine;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.entity.role.Role;

/**
 * Only for testing
 */
@Repository
public class TestEntityGeneratorService {

    @Autowired
    private GroomerRepository petSitterRepository;

    @Autowired
    private ParentRepository  petParentRepository;

    @Autowired
    private PoochRepository   petRepository;

    public Groomer getDBGroomer() {
        Groomer petSitter = getGroomer();
        return petSitterRepository.saveAndFlush(petSitter);
    }

    public Groomer getGroomer() {

        Groomer groomer = new Groomer();
        groomer.setUuid("groomer-" + UUID.randomUUID().toString());
        String firstName = RandomGeneratorUtils.getRandomFirstname();
        groomer.setFirstName(firstName);
        String lastName = RandomGeneratorUtils.getRandomLastname();
        groomer.setLastName(lastName);
        groomer.setEmail((firstName + "" + lastName).toLowerCase() + "@gmail.com");
        groomer.setEmailVerified(false);

        groomer.setNumberOfOccupancy(RandomGeneratorUtils.getLongWithin(2L, 100L));
        groomer.setChargePerMile(RandomGeneratorUtils.getDoubleWithin(1D, 3D));
        groomer.setOfferedDropOff(true);
        groomer.setOfferedPickUp(true);
        groomer.setStatus(GroomerStatus.SIGNING_UP);
        groomer.setSignUpStatus(GroomerSignUpStatus.ADD_SERVICES);
        groomer.setDescription("Test description");

        groomer.setPhoneNumber(RandomGeneratorUtils.getLongWithin(3101000000L, 3109999999L));
        groomer.setPhoneNumberVerified(false);

        groomer.setRating(RandomGeneratorUtils.getDoubleWithin(1, 5));
        groomer.addRole(new Role(Authority.groomer));

        groomer.addAddress(getAddress());

        return groomer;
    }

    public Parent getDBParent() {
        Parent petParent = getParent();
        return petParentRepository.saveAndFlush(petParent);
    }

    public Parent getParent() {

        Parent petParent = new Parent();
        petParent.setUuid("parent-" + UUID.randomUUID().toString());
        String firstName = RandomGeneratorUtils.getRandomFirstname();
        String lastName = RandomGeneratorUtils.getRandomLastname();
        petParent.setFullName(firstName + " " + lastName);
        petParent.setEmail((firstName + "" + lastName).toLowerCase() + "@gmail.com");
        petParent.setEmailVerified(false);
        petParent.setPhoneNumber(RandomGeneratorUtils.getLongWithin(3101000000L, 3109999999L));
        petParent.setPhoneNumberVerified(false);
        petParent.setRating(RandomGeneratorUtils.getDoubleWithin(1, 5));
        petParent.addRole(new Role(Authority.parent));

        petParent.setAddress(getAddress());

        return petParent;
    }

    public Pooch getDBPet(Parent petParent) {
        Pooch pet = getPet(petParent);
        return petRepository.saveAndFlush(pet);
    }

    public Pooch getPet() {
        return getPet(null);
    }

    public Pooch getPet(Parent petParent) {

        Pooch pet = new Pooch();
        pet.setBreed("Bulldog");
        pet.setParent(petParent);
        pet.setDob(LocalDate.now().minusMonths(RandomGeneratorUtils.getLongWithin(6, 60)));
        pet.addFoodSchedule(FoodSchedule.Morning);
        pet.addFoodSchedule(FoodSchedule.Night);
        pet.addVaccine(new Vaccine("vaccine", LocalDateTime.now().plusDays(24)));

        return pet;
    }

    public Address getAddress() {

        List<Address> addresses = new ArrayList<>();

        Address address = new Address();
        address.setCity("Santa Monica");
        address.setState("CA");
        address.setZipcode("90405");
        address.setStreet("3113 Pico Blvd");
        address.setLatitude(34.026150);
        address.setLongitude(-118.457170);

        addresses.add(address);

        address = new Address();
        address.setCity("Santa Monica");
        address.setState("CA");
        address.setZipcode("90404");
        address.setStreet("1222 26th St");
        address.setLatitude(34.035290);
        address.setLongitude(-118.477080);

        addresses.add(address);

        address = new Address();
        address.setCity("Santa Monica");
        address.setState("CA");
        address.setZipcode("90405");
        address.setStreet("1502 Maple St");
        address.setLatitude(34.011520);
        address.setLongitude(-118.469900);

        addresses.add(address);

        address = new Address();
        address.setCity("Santa Monica");
        address.setState("CA");
        address.setZipcode("90402");
        address.setStreet("835 San Vicente Blvd");
        address.setLatitude(34.034222);
        address.setLongitude(-118.507141);

        addresses.add(address);

        /** berverly hills */
        address = new Address();
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZipcode("90024");
        address.setStreet("719 S Beverly Glen Blvd");
        address.setLatitude(34.0698483);
        address.setLongitude(-118.4310435);

        addresses.add(address);

        address = new Address();
        address.setCity("Beverly Hills");
        address.setState("CA");
        address.setZipcode("90210");
        address.setStreet("620 Walden Dr");
        address.setLatitude(34.0703334);
        address.setLongitude(-118.4137745);

        addresses.add(address);

        address = new Address();
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZipcode("90025");
        address.setStreet("2062 Kerwood Ave");
        address.setLatitude(34.0535801);
        address.setLongitude(-118.41946);

        addresses.add(address);

        address = new Address();
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZipcode("90064");
        address.setStreet("2320 Manning Ave");
        address.setLatitude(34.0444587);
        address.setLongitude(-118.4239185);

        addresses.add(address);

        /** Los Angeles */
        address = new Address();
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZipcode("90019");
        address.setStreet("1129 Queen Anne Pl");
        address.setLatitude(34.0534423);
        address.setLongitude(-118.3308591);

        addresses.add(address);

        address = new Address();
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZipcode("90004");
        address.setStreet("237 N Berendo St");
        address.setLatitude(34.0755959);
        address.setLongitude(-118.294399);

        addresses.add(address);

        address = new Address();
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZipcode("90031");
        address.setStreet("3862 N Broadway");
        address.setLatitude(34.0737122);
        address.setLongitude(-118.197979);

        addresses.add(address);

        /**
         * 1800 Ocean Front Walk, Venice, CA 90291
         * 
         * 
         * 
         * 
         * 
         * 10:53 THIS IS THE LAT 33.9850469 this is the long -118.4694832
         */

        address = new Address();
        address.setCity("Venice");
        address.setState("CA");
        address.setZipcode("90291");
        address.setStreet("1800 Ocean Front Walk");
        address.setLatitude(33.9850469);
        address.setLongitude(-118.4694832);

        addresses.add(address);

        return addresses.get(RandomGeneratorUtils.getIntegerWithin(0, addresses.size() - 1));
    }
}
