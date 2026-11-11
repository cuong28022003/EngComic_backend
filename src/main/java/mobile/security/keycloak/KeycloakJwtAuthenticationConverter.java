package mobile.security.keycloak;

import lombok.RequiredArgsConstructor;
import mobile.common.AppUserRole;
import mobile.databases.entities.user.UserEntity;
import mobile.security.core.AppUserDetail;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps a Keycloak access token to an {@link AppUserDetail} principal so that
 * existing controllers, {@code @CurrentUser}/{@code @CurrentUserId} resolvers
 * and {@code @PreAuthorize} keep working unchanged.
 */
@Component
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final KeycloakUserService keycloakUserService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String subject = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        String fullName = jwt.getClaimAsString("name");
        Collection<String> realmRoles = extractRealmRoles(jwt);

        UserEntity user = keycloakUserService.ensureLinkedUser(
                subject, email, preferredUsername, fullName, realmRoles);

        AppUserDetail baseDetail = AppUserDetail.build(user);

        Set<GrantedAuthority> authorities = new HashSet<>(baseDetail.getAuthorities());
        Set<String> roles = new LinkedHashSet<>();
        if (baseDetail.getRoles() != null) {
            roles.addAll(baseDetail.getRoles());
        }

        for (String role : realmRoles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            for (AppUserRole item : AppUserRole.values()) {
                if (item.name().equalsIgnoreCase(role)) {
                    authorities.addAll(item.getGrantedAuthorities());
                }
            }
            roles.add(role);
        }

        AppUserDetail principal = new AppUserDetail(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                authorities,
                roles,
                user.getActive()
        );

        return new UsernamePasswordAuthenticationToken(principal, jwt.getTokenValue(), authorities);
    }

    @SuppressWarnings("unchecked")
    private Collection<String> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return List.of();
        }
        Object roles = realmAccess.get("roles");
        if (roles instanceof Collection<?> collection) {
            return collection.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }
}
