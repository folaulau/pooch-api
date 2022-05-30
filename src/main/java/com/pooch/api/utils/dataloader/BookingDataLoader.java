package com.pooch.api.utils.dataloader;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.booking.Booking;
import com.pooch.api.entity.booking.BookingRepository;
import com.pooch.api.entity.booking.BookingStatus;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentRepository;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochRepository;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;

@DependsOn(value = {"groomerDataLoader", "parentDataLoader"})
@Slf4j
@Profile(value = {"local", "dev", "qa"})
@Component
public class BookingDataLoader implements ApplicationRunner {

    @Autowired
    private TestEntityGeneratorService generatorService;

    @Autowired
    private BookingRepository          bookingRepository;

    @Autowired
    private PoochRepository            poochRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        long lastBookingId = 1;

        Optional<Booking> optParent = bookingRepository.findById(lastBookingId);

        if (optParent.isPresent()) {
            log.info("Booking seed data has been loaded already!");
            return;
        }

        try {

            Booking booking = null;

            for (int i = 0; i < lastBookingId; i++) {
                booking = new Booking();

                booking.setGroomer(Groomer.builder().id(1L).build());

                Parent parent = Parent.builder().id(1L).build();

                Pooch pooch = generatorService.getPooch();
                pooch.setId((long)(i+1));
                pooch.setParent(parent);

                poochRepository.saveAndFlush(pooch);

//                booking.addPooch(pooch);

                pooch = generatorService.getPooch();
                pooch.setId((long)(i+2));
                pooch.setParent(parent);

                poochRepository.saveAndFlush(pooch);

//                booking.addPooch(pooch);

                booking.setParent(Parent.builder().id(1L).build());
                booking.setStartDateTime(LocalDateTime.now().minusDays(RandomGeneratorUtils.getLongWithin(8, 10)));
                booking.setDropOffDateTime(booking.getStartDateTime().minusHours(1));

                booking.setEndDateTime(LocalDateTime.now().minusDays(RandomGeneratorUtils.getLongWithin(2, 4)));
                booking.setPickUpDateTime(booking.getEndDateTime().minusHours(1));
                
                booking.setStatus(BookingStatus.Booked);

                booking = bookingRepository.saveAndFlush(booking);

            }
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
        }

    }

}
