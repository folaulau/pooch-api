package com.pooch.api.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StripeAccountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String            id;
    private String            type;
}
