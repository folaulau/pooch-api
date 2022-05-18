package com.pooch.api.entity.booking;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.elastic.repo.AddressES;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.paymentmethod.PaymentMethod;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.vaccine.Vaccine;

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
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Booking + " SET deleted = 'T' WHERE id = ?", check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.Booking, indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class Booking implements Serializable {

    private static final long    serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long                 id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String               uuid;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "parent_id")
    private Parent               parent;

    @JsonIgnoreProperties(value = {"bookings", "parent"})
    @ManyToMany(cascade = {CascadeType.DETACH})
    @JoinTable(name = "booking_pooches", joinColumns = @JoinColumn(name = "booking_id"), inverseJoinColumns = @JoinColumn(name = "pooch_id"))
    private Set<Pooch>           pooches;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "groomer_id")
    private Groomer              groomer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus        status;

    @Enumerated(EnumType.STRING)
    @Column(name = "stripe_funds_status", nullable = true)
    private BookingStripeStatus  stripeFundsStatus;

    @Column(name = "pick_up_date_time", nullable = true)
    private LocalDateTime        pickUpDateTime;

    @Column(name = "drop_off_date_time", nullable = true)
    private LocalDateTime        dropOffDateTime;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime        startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime        endDateTime;

    @Column(name = "stripe_payment_intent_id", nullable = true)
    private String               stripePaymentIntentId;

    @Embedded
    private BookingPaymentMethod paymentMethod;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "debugging_notes")
    private String               debuggingNotes;

    @Column(name = "deleted", nullable = false)
    private boolean              deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime        createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime        updatedAt;

    public void addPooch(Pooch pooch) {
        if (this.pooches == null) {
            this.pooches = new HashSet<>();
        }
        this.pooches.add(pooch);
    }

    public void addDebuggingNote(String note) {
        StringBuilder notes = new StringBuilder((this.debuggingNotes != null) ? this.debuggingNotes : "");

        StringBuilder newNote = new StringBuilder();
        newNote.append("Timestamp: " + LocalDateTime.now().toString() + "\n");
        newNote.append("note: " + note + "\n");

        notes.append(newNote);

        this.debuggingNotes = notes.toString();

    }

    @PrePersist
    private void preCreate() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = "booking-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
        }

    }

}
