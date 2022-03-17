package com.pooch.api.dto;

import java.io.Serializable;
import java.util.Set;

import com.pooch.api.entity.pet.Breed;
import com.pooch.api.entity.pet.FoodSchedule;
import com.pooch.api.entity.pet.Gender;
import com.pooch.api.entity.pet.Training;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetCreateDTO implements Serializable {

    /**
     * 
     */
    private static final long     serialVersionUID = 1L;

    private String                uuid;

    private String                fullName;

    private Breed                 breed;

    private Gender                gender;

    private Training              training;

    private FoodSchedule          foodSchedule;

    private Integer               age;

    private Double                weight;

    private Boolean               spayed;

    private String                notes;

    private Set<VaccineCreateDTO> vaccines;
}
