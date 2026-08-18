package mobile.security.config;

public final class PublicSecurityEndpoints {
    private PublicSecurityEndpoints() {}

    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/actuator/**"
    };
}
