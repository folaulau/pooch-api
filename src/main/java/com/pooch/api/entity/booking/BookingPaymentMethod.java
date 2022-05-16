package com.pooch.api.entity.booking;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@JsonInclude(value = Include.NON_NULL)
@Embeddable
public class BookingPaymentMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "payment_method_id", nullable = true)
    private Long              id;

    @Column(name = "payment_method_uuid", nullable = true)
    private String            uuid;

    @Column(name = "payment_method_type", nullable = true)
    private String            type;

    @Column(name = "payment_method_name", nullable = true)
    private String            name;

    @Column(name = "payment_method_last4", nullable = true)
    private String            last4;

    @Column(name = "payment_method_brand", nullable = true)
    private String            brand;

    @Column(name = "payment_method_bank", nullable = true)
    private String            bank;

    @Column(name = "stripe_payment_method_id", nullable = true)
    private String            stripePaymentMethodId;

}
