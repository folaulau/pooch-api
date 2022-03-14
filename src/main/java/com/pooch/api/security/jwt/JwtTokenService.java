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
import com.pooch.api.entity.petparent.PetParent;
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
    private static final int         LIFE_TIME_IN_DAYS = 14;

    private static final JWTVerifier VERIFIER          = JWT.require(ALGORITHM).withIssuer(ISSUER).build();

    public String generateUserToken(PetParent petParent) {

        try {

            Map<String, Object> hasura = new HashMap<String, Object>();

            // Set<Role> roles = user.getRoles();
            //
            // boolean isPrimary = roles.stream().filter(r ->
            // r.getAuthority().equals(Authority.teacher)).findFirst().isPresent();
            //
            // hasura.put("x-hasura-allowed-roles", Authority.getAllAuths());
            // hasura.put("x-hasura-default-role", (isPrimary) ? Authority.teacher.name() : Authority.user.name());
            // hasura.put("x-Hasura-user-id", user.getId() + "");
            // hasura.put("x-Hasura-acct-id", user.getAccount().getId() + "");

            String token = JWT.create()
                    .withJWTId(RandomGeneratorUtils.getJWTId())
                    .withSubject(petParent.getId() + "")
                    .withExpiresAt(DateUtils.addDays(new Date(), LIFE_TIME_IN_DAYS))
                    .withIssuedAt(new Date())
                    .withAudience(audience)
                    .withIssuer(ISSUER)
                    // .withClaim("primary", isPrimary)
                    // .withClaim("uuid", user.getUuid())
                    // .withClaim("acud", user.getAccount().getUuid())
                    // .withClaim("name", user.getName())
                    // .withClaim("hasura", hasura)
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

    public JwtPayload getUserPayloadByToken(String token) {
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
                jwtPayload.setPrimary(jwt.getClaim("primary").asBoolean());
                jwtPayload.setName(jwt.getClaim("name").asString());
                jwtPayload.setUuid(jwt.getClaim("uuid").asString());
                jwtPayload.setAcud(jwt.getClaim("acud").asString());

                setHasura(jwtPayload, jwt);

                return jwtPayload;
            }
        } catch (Exception e) {
            log.warn("getPayloadByToken exception, msg: {}", e.getLocalizedMessage());
        }
        return null;
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

        try {
            List<String> roles = (List<String>) hasura.get("x-hasura-allowed-roles");

            jwtPayload.setRoles(new HashSet<>(roles));
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
        }

    }

}
