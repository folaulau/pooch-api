package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.pooch.api.entity.pet.Breed;
import com.pooch.api.entity.pet.FoodSchedule;
import com.pooch.api.entity.pet.Gender;
import com.pooch.api.entity.pet.Training;
import com.pooch.api.entity.pet.vaccine.Vaccine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO implements Serializable {

    /**
     * 
     */
    private static final long  serialVersionUID = 1L;

    private Long               id;

    private String             uuid;

    private String             fullName;

    private Breed              breed;

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
