package com.pooch.api.entity.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.utils.MathUtils;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingCalculatorServiceImp implements BookingCalculatorService {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${booking.fee:10}")
    private Double bookingFee;

    @Override
    public BookingCostDetails generatePaymentIntentDetails(Groomer groomer, Double amount) {

        BookingCostDetails costDetails = new BookingCostDetails();

        // $10 booking fee
        double bookingCost = MathUtils.getTwoDecimalPlaces(amount);

        // 2.9% of chargeAmount + 30 cents
        // put bookingCost on the parent, 10 booking is on pooch account
        double stripeFee = BigDecimal.valueOf(2.9)
                .divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(bookingCost))
                .add(BigDecimal.valueOf(0.3))
                .setScale(2, RoundingMode.CEILING)
                .doubleValue();


        double totalChargeToday = 0;
        double totalChargeAtDropOff = 0;

        if (groomer.isStripeReady()) {
            totalChargeToday = BigDecimal.valueOf(bookingCost).add(BigDecimal.valueOf(stripeFee)).add(BigDecimal.valueOf(bookingFee)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            totalChargeAtDropOff = 0;
        } else {
            totalChargeToday = bookingFee;
            totalChargeAtDropOff = bookingCost;
            stripeFee = 0;
        }


        costDetails.setBookingCost(bookingCost);
        costDetails.setBookingFee(bookingFee);
        costDetails.setStripeFee(stripeFee);
        costDetails.setTotalChargeAtBooking(totalChargeToday);
        costDetails.setTotalChargeAtDropOff(totalChargeAtDropOff);
        
        log.info("groomer={}",ObjectUtils.toJson(groomer));
        
        log.info("generatePaymentIntentDetails -> groomer.isStripeReady={}, bookingFee={}, bookingCost={}, totalChargeToday={}, stripeFee={}, totalChargeAtDropOff={}", groomer.isStripeReady(),  bookingFee, bookingCost, totalChargeToday, stripeFee,
                totalChargeAtDropOff);

        return costDetails;
    }

}
