package com.pooch.api.entity.notification.email;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.entity.notification.email.template.EmailTemplateUuid;
import com.pooch.api.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;


@Builder
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@Table(name = DatabaseTableNames.Email)
public class Email implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  @Column(name = "content")
  private String content;

  @Column(name = "subject")
  private String subject;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private EmailStatus status;

  @Column(name = "sender")
  private String sender;

  @Column(name = "send_to", length = 100)
  private String sendTo;

  @Column(name = "error", length = 15000)
  private String error;

  @Enumerated(EnumType.STRING)
  @Column(name = "template_uuid", nullable = false)
  private EmailTemplateUuid templateUuid;

  @Fetch(FetchMode.SELECT)
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(name = "cc")
  @CollectionTable(name = "email_carbon_copies", joinColumns = {@JoinColumn(name = "email_id")})
  private List<String> carbonCopies;

  @Fetch(FetchMode.SELECT)
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(name = "bcc")
  @CollectionTable(name = "email_blind_carbon_copies",
      joinColumns = {@JoinColumn(name = "email_id")})
  private List<String> blindCarbonCopies;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Transient
  private List<File> attachments;

  public void addCC(String cc) {
    if (this.carbonCopies == null) {
      this.carbonCopies = new ArrayList<>();
    }
    this.carbonCopies.add(cc);
  }

  public void addBCC(String bcc) {
    if (this.blindCarbonCopies == null) {
      this.blindCarbonCopies = new ArrayList<>();
    }
    this.blindCarbonCopies.add(bcc);
  }

  public void addAttachment(File attachment) {
    if (this.attachments == null) {
      this.attachments = new ArrayList<>();
    }
    this.attachments.add(attachment);
  }

  @PrePersist
  private void preCreate() {
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "email-" + new Date().getTime() + "-" + UUID.randomUUID().toString();
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
