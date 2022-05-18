package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;

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

    private LocalDate          dob;

    private Double             weight;

    private Boolean            spayed;

    private Boolean            neutered;

    private String             notes;

    private List<FoodSchedule> foodSchedule;

    private Set<VaccineDTO>    vaccines;

    private LocalDateTime      createdAt;

    private LocalDateTime      updatedAt;
}
