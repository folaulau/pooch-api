package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StripeAccountLinkDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime     expiredAt;
    private String            url;
}
