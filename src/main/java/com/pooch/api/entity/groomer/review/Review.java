package com.pooch.api.entity.groomer.review;

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
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.Review + " SET deleted = true WHERE id = ?", check = ResultCheckStyle.NONE)
@Where(clause = "deleted = true")
@Table(name = DatabaseTableNames.Review, indexes = {@Index(columnList = "uuid"), @Index(columnList = "parent_id"), @Index(columnList = "groomer_id"), @Index(columnList = "deleted")})
public class Review implements Serializable {

    private static final long serialVersionUID = 6634801783020254163L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long              id;

    @Column(name = "uuid", nullable = false, unique = true)
    private String            uuid;

    @Column(name = "rating", length = 6, precision = 2, scale = 2)
    private double            rating;
    //
    // @Column(name = "wait_time", length = 6, precision = 2, scale = 2)
    // private double waitTime;
    //
    // @Column(name = "bedside", length = 6, precision = 2, scale = 2)
    // private double bedside;
    //
    // @Column(name = "receipt", length = 6, precision = 2, scale = 2)
    // private double receipt;
    //
    // @Column(name = "negotiate", length = 6, precision = 2, scale = 2)
    // private double negotiate;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    private String            description;

    @Column(name = "groomer_id", nullable = false)
    private Long              groomerId;

    /**
     * pooch parent id
     */
    @Column(name = "parent_id", nullable = true)
    private Long              parentId;

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
            this.uuid = "review-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
        }
    }

}
