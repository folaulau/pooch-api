package com.pooch.api.utils;

public final class PathUtils {

    private PathUtils() {
    }

    public static final String[] PING_URLS        = {"/ping", "/ping/", "/", "/csrf"};

    public static final String[] DEMO_URLS        = {"/demos/**"};

    public static final String[] TEST_URLS        = {"/tests/**", "/integration"};

    public static final String[] PUBLIC_URLS      = {"/users/password-reset","/users/request-password-reset", "/exercises/question", "/curriculums/grade", "/users/signup", "/users/signup/", "/pub/**", "/jobs/**", "/jobs",
            "/payments/intent", "/payments/intent/"};

    public static final String[] SWAGGER_DOC_URLS = {"/v3/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources", "/swagger-resources/**", "/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/security", "/v2/api-docs", "/webjars/**", "/webjars/springfox-swagger-ui/**", "/webjars/springfox-swagger-ui/springfox.css?v=2.8.0-SNAPSHOT",
            "/webjars/springfox-swagger-ui/swagger-ui.css?v=2.8.0-SNAPSHOT"};

    public static final String[] LOGIN_URLS       = {"/users/signin"};
    public static final String[] LOGOUT_URLS      = {"/users/signout"};

}
