package mobile.security.config;

public final class PublicSecurityEndpoints {
    private PublicSecurityEndpoints() {}

    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api/translator/**",
            "/api/toeic/tests/file/**",
            "/api/toeic/tests/proxy-pdf",
            "/api/toeic/attempts/*/save-progress",
            "/uploads/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/actuator/**"
    };
}
