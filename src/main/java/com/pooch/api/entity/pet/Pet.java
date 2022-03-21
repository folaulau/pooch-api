package com.pooch.api.entity.pet;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.pet.vaccine.Vaccine;
import com.pooch.api.entity.petparent.PetParent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Pet + " SET deleted = 'T' WHERE id = ?", check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.Pet, indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class Pet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long              id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String            uuid;

    @Column(name = "full_name")
    private String            fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "breed")
    private Breed             breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender            gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "training")
    private Training          training;

    @ElementCollection
    @CollectionTable(name = "pet_food_schedule", joinColumns = {@JoinColumn(name = "pet_id")})
    private Set<FoodSchedule> foodSchedule;

    @Column(name = "dob")
    private LocalDate         dob;

    @Column(name = "weight")
    private Double            weight;

    /**
     * spayed or neutered
     */
    @Column(name = "spayed")
    private Boolean           spayed;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "notes")
    private String            notes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    private Set<Vaccine>      vaccines;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "pet_parent_id")
    private PetParent         petParent;

    @Column(name = "deleted", nullable = false)
    private boolean           deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime     createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime     updatedAt;

    public void addFoodSchedule(FoodSchedule fSchedule) {
        if (this.foodSchedule == null) {
            this.foodSchedule = new HashSet<>();
        }
        this.foodSchedule.add(fSchedule);
    }

    public void addVaccine(Vaccine vaccine) {
        if (this.vaccines == null) {
            this.vaccines = new HashSet<>();
        }
        this.vaccines.add(vaccine);
    }

    @PrePersist
    private void preCreate() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = "pet-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
        }

    }

}
