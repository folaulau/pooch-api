package com.pooch.api.security.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.pooch.api.utils.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * https://tools.ietf.org/html/rfc7519 JWT Payload <br>
 * 1. sub - user uid <br>
 * 
 * @author folaukaveinga
 *
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class JwtPayload implements Serializable {

    private static final long   serialVersionUID = 1L;

    // issuer
    private String              iss;

    // id of jwt
    private String              jti;

    // user id
    private String              sub;

    // user uuid
    private String              uuid;

    // project id - JwtTokenService.audience
    private String              aud;

    // account uuid
    private String              acud;

    // issued at
    private Date                iat;

    // expired at
    private Date                exp;

    // not before
    private Date                nbf;

    // admin user?
    private Boolean             admin;

    // primary user?
    private Boolean             primary;

    private String              name;

    private Set<String>         roles;

    /**
     * x-hasura-allowed-roles: [roles] - a list of allowed roles for the user i.e. acceptable values of the
     * x-hasura-role header. The x-hasura-default-role specified should be a member of this list.<br>
     * 
     * x-hasura-default-role: role - ndicating the default role of that user i.e. the role that will be used in case
     * x-hasura-role header is not passed.<br>
     * 
     * x-hasura-user-id: id<br>
     */
    private Map<String, Object> hasura;

    public void addHasuraClaim(String key, Object value) {
        if (this.hasura == null) {
            this.hasura = new HashMap<String, Object>();
        }
        this.hasura.put(key, value);
    }

    public String getDefaultRole() {
        try {
            return hasura.get("x-hasura-default-role").toString();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public String toJson() {
        try {
            return ObjectUtils.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.out.println("JwtPayload - toJson - JsonProcessingException, msg: " + e.getLocalizedMessage());
            return "{}";
        }
    }

    public boolean isAdmin() {
        return admin != null && admin.booleanValue() == true;
    }

}
