package com.pooch.api.entity.notification.email.template;

import java.io.Serializable;
import java.time.LocalDateTime;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.notification.Notification;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.security.ApiSession;
import com.pooch.api.utils.ApiSessionUtils;
import com.pooch.api.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.EmailTemplate + " SET deleted = 'T' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.EmailTemplate,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class EmailTemplate implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private EmailTemplateUuid uuid;

  /**
   * user this email template is for<br>
   * 
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "send_to_user")
  private UserType sendToUser;

  @Column(name = "subject")
  private String subject;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  @Column(name = "content")
  private String content;

  @CreationTimestamp
  @Column(name = "created_at", nullable = true)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = true)
  private LocalDateTime updatedAt;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  /*
   * uuid of the user creating this user
   */
  @Column(name = "created_by_user", updatable = false)
  private String createdByUser;

  /*
   * uuid of the user updating this user
   */
  @Column(name = "last_updated_by_user")
  private String lastUpdatedByUser;

  @JsonIgnoreProperties(value = {"emailTemplate"})
  @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
  @JoinColumn(name = "notification_id", nullable = false)
  private Notification notification;

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return ToStringBuilder.reflectionToString(this);
  }

  @PrePersist
  private void preCreate() {

    ApiSession currentUser = ApiSessionUtils.getApiSession();

    if (currentUser != null) {
      this.createdByUser = currentUser.getUserUuid();
      this.lastUpdatedByUser = currentUser.getUserUuid();
    } else {
      this.createdByUser = "system";
      this.lastUpdatedByUser = "system";
    }

  }

  @PreUpdate
  private void preUpdate() {
    ApiSession currentUser = ApiSessionUtils.getApiSession();

    if (currentUser != null) {
      this.lastUpdatedByUser = currentUser.getUserUuid();
    } else {
      this.lastUpdatedByUser = "system";
    }
  }

  public String toJson() {
    try {
      return ObjectUtils.toJson(this);
    } catch (Exception e) {
      log.warn("toJson error={}", e.getLocalizedMessage());
      return null;
    }
  }
}
