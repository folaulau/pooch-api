package com.pooch.api.entity.groomer.careservice.type;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;

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
@Table(name = DatabaseTableNames.CareServiceCategory)
public class GroomerServiceCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long              id;

    @Column(name = "uuid", nullable = false)
    private String            uuid;

    @Column(name = "name", unique = true)
    private String            name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CareServiceType   type;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    private String            description;

    public GroomerServiceCategory(String name, CareServiceType type) {
        this.name = name;
        this.type = type;
    }

    public GroomerServiceCategory(Long id, String name, CareServiceType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @PrePersist
    private void preCreate() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = "service-name-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
        }
    }
}
