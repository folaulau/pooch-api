package com.pooch.api.entity.booking;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class BookingCostDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * cost of the booking
     */
    private Double            bookingCost;

    /**
     * Pooch fee
     */
    private Double            bookingFee;

    /**
     * stripe fee on charge
     */
    private Double            stripeFee;

    /**
     * amount charge now depending on Groomer's Stripe status
     */
    private Double            totalChargeNowAmount;

    /**
     * amount charge at drop off depending on Groomer's Stripe status
     */
    private Double            totalChargeAtDropOffAmount;

}
