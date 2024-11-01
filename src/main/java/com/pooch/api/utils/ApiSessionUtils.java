package com.pooch.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.pooch.api.security.ApiSession;
import com.pooch.api.security.jwt.JwtPayload;

public interface ApiSessionUtils {

    static Logger log = LoggerFactory.getLogger(ApiSessionUtils.class);

    public static void setSessionToken(WebAuthenticationDetails authDetails, ApiSession apiSession) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (apiSession.getUserRoles() != null || apiSession.getUserRoles().isEmpty() == false) {
            for (String role : apiSession.getUserRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
            }
        }

        UsernamePasswordAuthenticationToken updateAuth = new UsernamePasswordAuthenticationToken(apiSession, apiSession.getUserUuid(), authorities);

        updateAuth.setDetails(authDetails);

        SecurityContextHolder.getContext().setAuthentication(updateAuth);
    }

    public static void setSessionToken(WebAuthenticationDetails authDetails, JwtPayload jwtPayload) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (jwtPayload.getRole() != null || jwtPayload.getRole().isEmpty() == false) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + jwtPayload.getRole().toUpperCase()));
        }

        UsernamePasswordAuthenticationToken updateAuth = new UsernamePasswordAuthenticationToken(jwtPayload, jwtPayload.getUuid(), authorities);

        updateAuth.setDetails(authDetails);

        SecurityContextHolder.getContext().setAuthentication(updateAuth);
    }

    public static JwtPayload getJwtPayload() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                JwtPayload session = (JwtPayload) auth.getPrincipal();
                return session;
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

        }
        return null;
    }

    public static ApiSession getApiSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                ApiSession session = (ApiSession) auth.getPrincipal();
                return session;
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

        }
        return null;
    }

    public static Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                ApiSession session = (ApiSession) auth.getPrincipal();
                return session.getUserId();
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

        }
        return null;
    }

    public static Long getAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                ApiSession session = (ApiSession) auth.getPrincipal();
                return session.getAccountId();
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

        }
        return null;
    }

    public static String getAccountUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                ApiSession session = (ApiSession) auth.getPrincipal();
                return session.getAccountUuid();
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

        }
        return null;
    }

    public static String getUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                ApiSession session = (ApiSession) auth.getPrincipal();
                return session.getUserUuid();
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

        }
        return null;
    }

    public static String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                ApiSession session = (ApiSession) auth.getPrincipal();
                return session.getUserUuid();
            } catch (Exception e) {
                log.warn("Exception, msg={}", e.getLocalizedMessage());
            }

        }
        return "SYSTEM";
    }

    public static String getActiveProfile(Environment environment) {
        String env = "";
        try {
            env = Arrays.asList(environment.getActiveProfiles()).get(0);
        } catch (Exception e) {
        }
        return env;
    }

}
