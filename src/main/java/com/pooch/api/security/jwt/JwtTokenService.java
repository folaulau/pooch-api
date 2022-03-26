package com.pooch.api.security.jwt;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.role.Authority;
import com.pooch.api.entity.role.Role;
import com.pooch.api.utils.RandomGeneratorUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenService {

    private static final String      secret            = "poochapi-poochapi-poochapi-poochapi-poochapi";

    private static final String      audience          = "poochapi";

    private static final Algorithm   ALGORITHM         = Algorithm.HMAC256(secret);

    private static final String      ISSUER            = "poochapi";

    /**
     * user can stay logged in for 14 days
     */
    private static final int         LIFE_TIME_IN_DAYS = 200;

    private static final JWTVerifier VERIFIER          = JWT.require(ALGORITHM).withIssuer(ISSUER).build();

    public String generatePetParentToken(Parent petParent) {

        try {

            Map<String, Object> hasura = new HashMap<String, Object>();

            hasura.put("x-hasura-allowed-roles", Authority.getAllAuths());
            hasura.put("x-hasura-default-role", Authority.parent.name());
            hasura.put("x-Hasura-parent-id", petParent.getId() + "");
            hasura.put("x-Hasura-parent-uuid", petParent.getUuid());

            String token = JWT.create()
                    .withJWTId(RandomGeneratorUtils.getJWTId())
                    .withSubject(petParent.getId() + "")
                    .withExpiresAt(DateUtils.addDays(new Date(), LIFE_TIME_IN_DAYS))
                    .withIssuedAt(new Date())
                    .withAudience(audience)
                    .withIssuer(ISSUER)
                    .withClaim("uuid", petParent.getUuid())
                    .withClaim("name", petParent.getFullName())
                    .withClaim("role", Authority.parent.name())
                    .withClaim("hasura", hasura)
                    .sign(ALGORITHM);
            return token;
        } catch (JWTCreationException e) {
            log.warn("JWTCreationException, msg: {}", e.getLocalizedMessage());
            return null;
        } catch (Exception e) {
            log.warn("generateToken exception, msg: {}", e.getLocalizedMessage());
            return null;
        }

    }

    public JwtPayload getPayloadByToken(String token) {
        if (token == null || token.length() == 0) {
            return null;
        }

        try {

            // Reusable verifier instance
            DecodedJWT jwt = VERIFIER.verify(token);
            if (jwt != null) {
                JwtPayload jwtPayload = new JwtPayload();
                jwtPayload.setExp(jwt.getExpiresAt());
                jwtPayload.setIss(jwt.getIssuer());
                jwtPayload.setJti(jwt.getId());
                jwtPayload.setIat(jwt.getIssuedAt());
                jwtPayload.setSub(jwt.getSubject());
                jwtPayload.setAud(jwt.getAudience().get(0));
                jwtPayload.setName(jwt.getClaim("name").asString());
                jwtPayload.setUuid(jwt.getClaim("uuid").asString());
                jwtPayload.setRole(jwt.getClaim("role").asString());

                setHasura(jwtPayload, jwt);

                return jwtPayload;
            }
        } catch (Exception e) {
            log.warn("getPayloadByToken exception, msg: {}", e.getLocalizedMessage());
        }
        return null;
    }

    public String generateGroomerToken(Groomer groomer) {

        try {

            Map<String, Object> hasura = new HashMap<String, Object>();

            Set<Role> roles = groomer.getRoles();

            hasura.put("x-hasura-allowed-roles", Authority.getAllAuths());
            hasura.put("x-hasura-default-role", Authority.groomer.name());
            hasura.put("x-Hasura-groomer-id", groomer.getId() + "");
            hasura.put("x-Hasura-groomer-uuid", groomer.getUuid());

            String token = JWT.create()
                    .withJWTId(RandomGeneratorUtils.getJWTId())
                    .withSubject(groomer.getId() + "")
                    .withExpiresAt(DateUtils.addDays(new Date(), LIFE_TIME_IN_DAYS))
                    .withIssuedAt(new Date())
                    .withAudience(audience)
                    .withIssuer(ISSUER)
                    .withClaim("uuid", groomer.getUuid())
                    .withClaim("name", groomer.getFullName())
                    .withClaim("role", Authority.groomer.name())
                    .withClaim("hasura", hasura)
                    .sign(ALGORITHM);
            return token;
        } catch (JWTCreationException e) {
            log.warn("JWTCreationException, msg: {}", e.getLocalizedMessage());
            return null;
        } catch (Exception e) {
            log.warn("generateToken exception, msg: {}", e.getLocalizedMessage());
            return null;
        }
    }

    private void setHasura(JwtPayload jwtPayload, DecodedJWT jwt) {
        Map<String, Object> hasura = null;
        try {
            hasura = jwt.getClaim("hasura").asMap();
            jwtPayload.setHasura(hasura);
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
        }

        if (hasura == null) {
            return;
        }

    }

}
