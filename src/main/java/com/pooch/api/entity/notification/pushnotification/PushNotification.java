package com.pooch.api.entity.notification.pushnotification;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;
import com.pooch.api.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@Table(name = DatabaseTableNames.PushNotification)
public class PushNotification implements Serializable {
  private static final long serialVersionUID = 1L;

  @ToString.Include
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "navigation_enum")
  private String navigationEnum;

  @Column(name = "model_type")
  private String modelType;

  @Column(name = "model_uuid")
  private String modelUuid;

  @Column(name = "payload", length = 15000)
  private String payload;

  @Column(name = "status")
  private String status;

  @Type(type = "true_false")
  @Column(name = "viewed")
  private Boolean viewed;

  @Column(name = "sent_to_ios", updatable = false)
  private Date sentToIos;

  @Column(name = "sent_to_web", updatable = false)
  private Date sentToWeb;

  @Column(name = "sent_to_android", updatable = false)
  private Date sentToAndroid;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;


  public String toJson() {
    try {
      return ObjectUtils.toJson(this);
    } catch (Exception e) {
      log.warn("toJson, msg={}", e.getLocalizedMessage());
      return null;
    }
  }
}
