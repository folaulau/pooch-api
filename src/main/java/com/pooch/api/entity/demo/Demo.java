package com.pooch.api.entity.demo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
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
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.entity.role.Role;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Demo + " SET deleted = true WHERE id = ?", check = ResultCheckStyle.NONE)
@Where(clause = "deleted = false")
@Table(name = DatabaseTableNames.Demo, indexes = {@Index(columnList = "uuid"), @Index(columnList = "email"), @Index(columnList = "deleted")})
public class Demo implements Serializable {

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

    @Column(name = "email")
    private String            email;

    @Column(name = "phone_number")
    private String            phoneNumber;

    @Column(name = "company_name")
    private String            companyName;

    @Column(name = "company_website")
    private String            companyWebsite;

    @Column(name = "number_of_pets_per_day")
    private String           numberOfPetsPerDay;

    @ElementCollection
    @CollectionTable(name = "demo_service_offer", joinColumns = @JoinColumn(name = "demo_id"))
    @Column(name = "service")
    private Set<String>       services;

    @Column(name = "deleted")
    private boolean           deleted;

    @Column(name = "marketing_communication_consent")
    private boolean           marketingCommunicationConsent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime     createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime     updatedAt;

    @PrePersist
    private void preCreate() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = "demo-" + UUID.randomUUID().toString();
        }
    }

}
