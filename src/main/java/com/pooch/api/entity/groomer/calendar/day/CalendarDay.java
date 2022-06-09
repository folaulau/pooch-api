package com.pooch.api.entity.groomer.calendar.day;

import java.io.Serializable;
import java.time.LocalDate;
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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.groomer.Groomer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicInsert
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.CalendarDay + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.CalendarDay, indexes = {@Index(columnList = "uuid"),
    @Index(columnList = "deleted"), @Index(columnList = "date")})
public class CalendarDay implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @Column(name = "date", nullable = true)
  private LocalDate date;

  @Column(name = "operational", columnDefinition = "boolean default false")
  private Boolean operational;

  @Column(name = "filled", columnDefinition = "boolean default false")
  private Boolean filled;

  @Column(name = "number_of_bookings", columnDefinition = "integer default 0", nullable = true)
  private Integer numberOfBookings;

  @Column(name = "number_of_openings", columnDefinition = "integer default 0", nullable = true)
  private Integer numberOfOpenings;

  // add list of bookings
  @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "groomer_id", nullable = true)
  private Groomer groomer;

  // @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  // @JoinColumn(name = "calendar_id", nullable = true)
  // private Calendar calendar;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;


  @PrePersist
  private void preCreate() {
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "calendarday-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
    }

  }

  public void generateFill(Long numberOfOccupancy) {
    if (numberOfBookings == null) {
      this.filled = false;
    } else {
      this.filled = (numberOfBookings >= numberOfOccupancy.intValue());
    }
  }

  public void addBookingCount(Long numberOfOccupancy) {
    if (numberOfBookings == null) {
      numberOfBookings = 0;
    }
    numberOfBookings++;

    if (numberOfOccupancy != null) {
      numberOfOpenings = numberOfOccupancy.intValue() - numberOfBookings;
    }


  }

}
