package com.pooch.api.entity.petsitter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.PetSitter + " SET deleted = 'T' WHERE id = ?", check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.PetSitter, indexes = {@Index(columnList = "uuid"), @Index(columnList = "email"), @Index(columnList = "deleted")})
public class PetSitter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long              id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String            uuid;

    @Column(name = "first_name")
    private String            firstName;

    @Column(name = "last_name")
    private String            lastName;

    @NotEmpty
    @Column(name = "email", unique = true)
    private String            email;

    @Column(name = "email_verified")
    private Boolean           emailVerified;

    @Column(name = "phone_number")
    private Long              phoneNumber;

    @Column(name = "phone_number_verified")
    private Boolean           phoneNumberVerified;

    /**
     * 5 star rating
     */
    @Column(name = "rating")
    private Integer           rating;

    @Column(name = "offered_pickup_off")
    private Boolean           offeredPickUp;

    @Column(name = "offered_drop_off")
    private Boolean           offeredDropOff;

    @Column(name = "charge_per_mile")
    private Double            chargePerMile;

    @Column(name = "number_of_ocupancy")
    private Long              numberOfOcupancy;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    private String            description;

    @Column(name = "deleted", nullable = false)
    private boolean           deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime     createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime     updatedAt;

    @PrePersist
    private void preCreate() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = "petsitter-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    private void preUpdate() {
    }

}
