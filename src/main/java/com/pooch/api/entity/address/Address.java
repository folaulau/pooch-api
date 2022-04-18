package com.pooch.api.entity.address;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@Table(name = DatabaseTableNames.Address)
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long              id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String            uuid;

    @Column(name = "street")
    private String            street;

    @Column(name = "street2")
    private String            street2;

    @Column(name = "city")
    private String            city;

    @Column(name = "state")
    private String            state;

    @Column(name = "zipcode")
    private String            zipcode;

    @Column(name = "country")
    private String            country;

    @Column(name = "longitude")
    private Double            longitude;

    @Column(name = "latitude")
    private Double            latitude;

    @Column(name = "timezone")
    private String            timezone;

    @Column(name = "primary_address")
    private boolean           primary;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id", nullable = true)
    private Groomer           groomer;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Parent            parent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime     createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime     updatedAt;

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.id)
                .append(this.uuid)
                .append(this.street)
                .append(this.street2)
                .append(this.city)
                .append(this.state)
                .append(this.zipcode)
                .append(this.country)
                .append(this.primary)
                .toHashCode();

        // return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Address other = (Address) obj;
        return new EqualsBuilder().append(this.id, other.id)
                .append(this.uuid, other.uuid)
                .append(this.street, other.street)
                .append(this.street2, other.street2)
                .append(this.city, other.city)
                .append(this.state, other.state)
                .append(this.zipcode, other.zipcode)
                .append(this.country, other.country)
                .append(this.primary, other.primary)
                .isEquals();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return ToStringBuilder.reflectionToString(this);
    }

    @PrePersist
    private void preCreate() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = "address-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    private void preUpdate() {
    }

}
