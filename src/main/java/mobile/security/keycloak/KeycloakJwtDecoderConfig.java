package mobile.security.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a {@link JwtDecoder} for Keycloak that validates signature (JWKS),
 * issuer and audience. Using an explicit JWK set URI (instead of issuer-uri)
 * avoids a network call to Keycloak during application startup, so the backend
 * can still boot when Keycloak is temporarily unavailable.
 */
@Configuration
public class KeycloakJwtDecoderConfig {

    @Value("${app.keycloak.issuer-uri:http://localhost:8082/realms/myapp}")
    private String issuerUri;

    @Value("${app.keycloak.jwk-set-uri:http://localhost:8082/realms/myapp/protocol/openid-connect/certs}")
    private String jwkSetUri;

    @Value("${app.keycloak.audience:my-backend}")
    private String audience;

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(JwtValidators.createDefaultWithIssuer(issuerUri));

        if (StringUtils.hasText(audience)) {
            validators.add(new JwtClaimValidator<List<String>>("aud",
                    aud -> aud != null && aud.contains(audience)));
        }

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));
        return decoder;
    }
}
