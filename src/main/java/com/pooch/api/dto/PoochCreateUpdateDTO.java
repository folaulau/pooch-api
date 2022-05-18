package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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
public class PoochCreateUpdateDTO implements Serializable {

    /**
     * 
     */
    private static final long     serialVersionUID = 1L;

    private String                uuid;

    private String                fullName;

    private String                breed;

    private Gender                gender;

    private Training              training;

    private Set<FoodSchedule>     foodSchedule;

    private LocalDate             dob;

    private Double                weight;

    private Boolean               spayed;

    private String                notes;

    private Set<VaccineCreateDTO> vaccines;

    public void addFoodSchedule(FoodSchedule fSchedule) {
        if (foodSchedule == null) {
            foodSchedule = new HashSet<>();
        }
        foodSchedule.add(fSchedule);
    }

    public void addVaccine(VaccineCreateDTO vaccineCreateDTO) {
        if (vaccines == null) {
            vaccines = new HashSet<>();
        }
        vaccines.add(vaccineCreateDTO);
    }
}
