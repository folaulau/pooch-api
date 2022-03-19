package com.pooch.api.entity.petcare;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.pet.FoodSchedule;
import com.pooch.api.entity.pet.Pet;
import com.pooch.api.entity.pet.vaccine.Vaccine;
import com.pooch.api.entity.petcare.careservice.PetCareService;
import com.pooch.api.entity.petparent.PetParent;
import com.pooch.api.entity.petsitter.PetSitter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.PetCare + " SET deleted = 'T' WHERE id = ?", check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.PetCare, indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class PetCare implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long              id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String            uuid;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "pet_parent_id")
    private PetSitter         petParent;

    // @JsonIgnoreProperties(value = {"expenses", "scrubbedData"})
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
    @JoinTable(name = "petcare_pets", joinColumns = @JoinColumn(name = "petcare_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "pet_id", referencedColumnName = "id"))
    private Set<Pet>          pets;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "pet_sitter_id")
    private PetSitter         petSitter;

    // @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
    // @JoinTable(name = "petcare_pets", joinColumns = @JoinColumn(name = "petcare_id", referencedColumnName = "id"),
    // inverseJoinColumns = @JoinColumn(name = "pet_id", referencedColumnName = "id"))
    // private Set<PetCareService> petCareServices;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PetCareStatus     status;

    @Column(name = "pick_up_date_time", nullable = false)
    private LocalDateTime     pickUpDateTime;

    @Column(name = "deleted", nullable = false)
    private boolean           deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime     createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime     lastUpdatedAt;

}
