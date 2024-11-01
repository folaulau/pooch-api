package com.pooch.api.entity.groomer.careservice;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.elastic.repo.AddressES;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.pooch.PoochSize;
import com.pooch.api.utils.MathUtils;

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
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.CareService + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.CareService,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class CareService implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @Column(name = "name")
  private String name;

  /**
   * 1-20LB
   */
  @Column(name = "small_price")
  private Double smallPrice;

  @Column(name = "service_small")
  private boolean serviceSmall;

  /**
   * 21-40LB
   */
  @Column(name = "medium_price")
  private Double mediumPrice;

  @Column(name = "service_medium")
  private boolean serviceMedium;

  /**
   * 41LB +
   */
  @Column(name = "large_price")
  private Double largePrice;

  @Column(name = "service_large")
  private boolean serviceLarge;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  @Column(name = "description")
  private String description;

  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "groomer_id", nullable = false)
  private Groomer groomer;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public void setSmallPrice(Double smallPrice) {
    if (smallPrice == null) {
      return;
    }
    this.smallPrice = MathUtils.getTwoDecimalPlaces(smallPrice);
  }

  public void setMediumPrice(Double mediumPrice) {
    if (mediumPrice == null) {
      return;
    }
    this.mediumPrice = MathUtils.getTwoDecimalPlaces(mediumPrice);
  }

  public void setLargePrice(Double largePrice) {
    if (largePrice == null) {
      return;
    }
    this.largePrice = MathUtils.getTwoDecimalPlaces(largePrice);
  }

  public Double getByPoochSize(String poochSize) {
    if (poochSize == null || poochSize.trim().isEmpty()) {
      return null;
    }

    if (poochSize.equalsIgnoreCase("small")) {
      return this.smallPrice;
    } else if (poochSize.equalsIgnoreCase("medium")) {
      return this.mediumPrice;
    } else if (poochSize.equalsIgnoreCase("large")) {
      return this.largePrice;
    }
    return null;
  }

  @PrePersist
  private void preCreate() {
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "care-service-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
    }
  }

  public Double getPriceBySize(String size) {
    if (size == null || size.length() == 0 || !PoochSize.isValidSize(size)) {
      return null;
    }

    if (size.equalsIgnoreCase(PoochSize.small)) {
      return this.smallPrice;
    } else if (size.equalsIgnoreCase(PoochSize.medium)) {
      return this.mediumPrice;
    } else if (size.equalsIgnoreCase(PoochSize.large)) {
      return this.largePrice;
    }
    return null;
  }

  public boolean isSizeServiced(String size) {
    if (size == null || size.length() == 0 || !PoochSize.isValidSize(size)) {
      return false;
    }

    if (size.equalsIgnoreCase(PoochSize.small)) {
      return this.serviceSmall;
    } else if (size.equalsIgnoreCase(PoochSize.medium)) {
      return this.serviceMedium;
    } else if (size.equalsIgnoreCase(PoochSize.large)) {
      return this.serviceLarge;
    }
    return false;
  }

}
