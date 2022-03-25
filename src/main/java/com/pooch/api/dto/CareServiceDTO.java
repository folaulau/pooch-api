package com.pooch.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class CareServiceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            uuid;

    private String            name;

    /**
     * 1-20LB
     */
    private Double            smallPrice;

    /**
     * 21-40LB
     */
    private Double            mediumPrice;

    /**
     * 41LB +
     */
    private Double            largePrice;

    private LocalDateTime     createdAt;

    private LocalDateTime     updatedAt;

}
