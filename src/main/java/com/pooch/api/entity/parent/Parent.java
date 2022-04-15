package com.pooch.api.entity.parent;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.OneToOne;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.elastic.repo.AddressES;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.entity.role.Role;

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
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Parent + " SET deleted = 'T' WHERE id = ?", check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.Parent, indexes = {@Index(columnList = "uuid"), @Index(columnList = "email"), @Index(columnList = "phone_number"), @Index(columnList = "deleted")})
public class Parent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long              id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String            uuid;

    @Column(name = "full_name")
    private String            fullName;

    /**
     * 5 star rating
     */
    @Column(name = "rating")
    private Double            rating;

    @NotEmpty
    @Column(name = "email", unique = true)
    private String            email;

    @Column(name = "email_verified")
    private Boolean           emailVerified;

    /**
     * Social platforms(facebook, google, etc) don't give email<br>
     * Now create a temp email for now
     */
    @Column(name = "email_temp")
    private boolean           emailTemp;

    /**
     * assume all phone numbers are US numbers for now.
     */
    @Column(name = "phone_number")
    private Long              phoneNumber;

    @Column(name = "phone_number_verified")
    private Boolean           phoneNumberVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParentStatus      status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address           address;

    @JsonIgnoreProperties(value = {"parents"})
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "parent_roles", joinColumns = {@JoinColumn(name = "parent_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role>         roles;

    @Column(name = "deleted", nullable = false)
    private boolean           deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime     createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime     updatedAt;

    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    public String getRoleAsString() {
        if (this.roles == null) {
            return null;
        }
        return this.roles.stream().findFirst().get().getAuthority().name();
    }

    public boolean isActive() {
        return Optional.ofNullable(this.status).orElse(ParentStatus.NONE).equals(ParentStatus.ACTIVE);
    }

    @PrePersist
    private void preCreate() {
        if (this.status == null) {
            this.status = ParentStatus.ACTIVE;
        }
    }

}
