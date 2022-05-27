package com.pooch.api.entity.parent.paymentmethod;

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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.parent.Parent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.PaymentMethod + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.PaymentMethod, indexes = {@Index(columnList = "uuid")})
public class PaymentMethod implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  /**
   * card, bank
   */
  @Column(name = "type", updatable = false, nullable = false)
  private String type;

  @Column(name = "uuid", unique = true, updatable = false, nullable = false)
  private String uuid;

  @Column(name = "name")
  private String name;

  @Column(name = "last4")
  private String last4;

  @Column(name = "brand")
  private String brand;

  @Column(name = "source_token")
  private String sourceToken;

  @Column(name = "stripe_id")
  private String stripeId;

  @Column(name = "expiration_month")
  private Long expirationMonth;

  @Column(name = "expiration_year")
  private Long expirationYear;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @JsonIgnoreProperties(value = {"address", "roles"})
  @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", nullable = false, updatable = false)
  private Parent parent;

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.id).append(this.uuid).append(this.parent)
        .toHashCode();
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
    PaymentMethod other = (PaymentMethod) obj;
    return new EqualsBuilder().append(this.id, other.id).append(this.uuid, other.uuid)
        .append(this.parent, other.parent).isEquals();
  }

  @PrePersist
  @PreUpdate
  private void preCreateUpdate() {
    if (this.type == null) {
      this.type = "card";
    }
    if (this.uuid == null || this.uuid.length() == 0) {
      this.uuid = "payment-method-" + +new Date().getTime() + "-" + UUID.randomUUID().toString();
    }
  }

}
