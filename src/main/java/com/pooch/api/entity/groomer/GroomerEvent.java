package com.pooch.api.entity.groomer;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroomerEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

}
