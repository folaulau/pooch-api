package com.pooch.api.elastic.repo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Dynamic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.role.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@Document(indexName = "groomer", dynamic = Dynamic.TRUE)
public class GroomerES implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private String uuid;

	private String firstName;

	private String lastName;

	private String businessName;

	private String email;

	private Boolean emailVerified;

	private boolean emailTemp;

	private Long phoneNumber;

	private Boolean phoneNumberVerified;

	private Double rating;

	private Boolean offeredPickUp;

	private Boolean offeredDropOff;

	private Double chargePerMile;

	private Long numberOfOcupancy;

	private String description;

	private Boolean instantBooking;

	private boolean deleted;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
