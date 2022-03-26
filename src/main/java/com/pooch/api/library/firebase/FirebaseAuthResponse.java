package com.pooch.api.library.firebase;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseAuthResponse implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            kind;
    private String            localId;
    private String            email;
    private String            idToken;
    private String            displayName;
}
