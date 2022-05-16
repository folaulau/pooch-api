package com.pooch.api.dto;

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
public class BookingPaymentMethodDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            uuid;

    private String            type;

    private String            name;

    private String            last4;

    private String            brand;

    private String            bank;

    private String            stripePaymentMethodId;

}
