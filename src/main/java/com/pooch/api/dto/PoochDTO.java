package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.Training;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoochDTO implements Serializable {

    /**
     * 
     */
    private static final long  serialVersionUID = 1L;

    private Long               id;

    private String             uuid;

    private String             fullName;

    private String             breed;

    private Gender             gender;

    private Training           training;

    private List<FoodSchedule> foodSchedule;

    private Integer            age;

    private Double             weight;

    private Boolean            spayed;

    private String             notes;

    private Set<VaccineDTO>    vaccines;

    private LocalDateTime      createdAt;

    private LocalDateTime      updatedAt;
}
