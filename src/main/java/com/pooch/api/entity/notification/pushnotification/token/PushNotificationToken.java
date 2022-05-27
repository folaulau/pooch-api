package com.pooch.api.entity.notification.pushnotification.token;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;

@JsonInclude(value = Include.NON_NULL)
@Entity
@Table(name = DatabaseTableNames.PushNotificationToken)
public class PushNotificationToken implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "groomer_id", nullable = true)
  private Long groomerId;

  @Column(name = "parent_id", nullable = true)
  private Long parentId;

  @Column(name = "token", updatable = false, nullable = false, unique = true)
  private String token;

  @Column(name = "device_type", updatable = false, nullable = false)
  private String deviceType;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", updatable = false, nullable = false)
  private Date createdAt;

  @Type(type = "true_false")
  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @Column(name = "deleted_at")
  private Date deletedAt;

}
