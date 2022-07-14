package com.pooch.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.validators.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.Valid;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class GroomerAvailabilityCreateUpdateDTO implements Serializable {

    private static final long         serialVersionUID = 1L;

    @NotNull(message = "uuid is required")
    private String                    uuid;

    @NotNull(message = "operateMonday is required")
    private Boolean operateMonday;

    @NotNull(message = "operateTuesday is required")
    private Boolean operateTuesday;

    @NotNull(message = "operateWednesday is required")
    private Boolean operateWednesday;

    @NotNull(message = "operateThursday is required")
    private Boolean operateThursday;

    @NotNull(message = "operateFriday is required")
    private Boolean operateFriday;

    @NotNull(message = "operateSaturday is required")
    private Boolean operateSaturday;

    @NotNull(message = "operateSunday is required")
    private Boolean operateSunday;

    @Schema(type="string" , format = "time", example = "00:00:00")
    @NotNull(message = "openTime is required")
    private LocalTime openTime;

    @Schema(type="string" , format = "time", example = "00:00:00")
    @NotNull(message = "closeTime is required")
    private LocalTime closeTime;
}
