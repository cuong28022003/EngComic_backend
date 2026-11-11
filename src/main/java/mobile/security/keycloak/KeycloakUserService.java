package mobile.security.keycloak;

import lombok.RequiredArgsConstructor;
import mobile.databases.entities.user.RoleEntity;
import mobile.databases.entities.user.UserEntity;
import mobile.databases.repositories.user.RoleRepository;
import mobile.databases.repositories.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Bridges Keycloak identities (claim {@code sub}) with the application's
 * MongoDB user table. Used by the resource-server JWT converter so that the
 * rest of the application keeps working with {@code AppUserDetail} and the
 * existing Mongo user id.
 */
@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private static final List<String> MANAGED_ROLES = List.of("USER", "ADMIN", "TRANSLATOR");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserEntity ensureLinkedUser(String keycloakId,
                                       String email,
                                       String preferredUsername,
                                       String fullName,
                                       Collection<String> realmRoles) {
        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException("Missing Keycloak subject (sub) claim");
        }

        boolean dirty = false;
        UserEntity user = userRepository.findByKeycloakId(keycloakId).orElse(null);

        if (user == null && email != null && !email.isBlank()) {
            user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                user.setKeycloakId(keycloakId);
                dirty = true;
            }
        }

        if (user == null && preferredUsername != null && !preferredUsername.isBlank()) {
            user = userRepository.findByUsername(preferredUsername).orElse(null);
            if (user != null) {
                user.setKeycloakId(keycloakId);
                dirty = true;
            }
        }

        if (user == null) {
            user = createUser(keycloakId, email, preferredUsername, fullName);
            dirty = true;
        } else if (syncIdentity(user, email, preferredUsername)) {
            dirty = true;
        }

        if (syncRoles(user, realmRoles)) {
            dirty = true;
        }

        return dirty ? userRepository.save(user) : user;
    }

    private UserEntity createUser(String keycloakId, String email, String preferredUsername, String fullName) {
        String username = resolveUniqueUsername(keycloakId, email, preferredUsername);
        return UserEntity.builder()
                .keycloakId(keycloakId)
                .username(username)
                .email(email)
                .fullName(fullName != null && !fullName.isBlank() ? fullName : username)
                .createdate(new Date())
                .active(true)
                .status("ACTIVE")
                .roles(new HashSet<>())
                .build();
    }

    private String resolveUniqueUsername(String keycloakId, String email, String preferredUsername) {
        String base = preferredUsername;
        if (base == null || base.isBlank()) {
            if (email != null && email.contains("@")) {
                base = email.substring(0, email.indexOf('@'));
            } else {
                base = "kc_" + keycloakId.substring(0, Math.min(8, keycloakId.length()));
            }
        }

        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + "_" + keycloakId.substring(0, Math.min(6, keycloakId.length()))
                    + (suffix > 1 ? String.valueOf(suffix) : "");
            suffix++;
            if (suffix > 50) {
                break;
            }
        }
        return candidate;
    }

    private boolean syncIdentity(UserEntity user, String email, String preferredUsername) {
        boolean changed = false;

        if (email != null && !email.isBlank() && !email.equalsIgnoreCase(user.getEmail())) {
            UserEntity emailOwner = userRepository.findByEmail(email).orElse(null);
            if (emailOwner == null || emailOwner.getId().equals(user.getId())) {
                user.setEmail(email);
                changed = true;
            }
        }

        if (preferredUsername != null && !preferredUsername.isBlank()
                && !preferredUsername.equals(user.getUsername())) {
            UserEntity usernameOwner = userRepository.findByUsername(preferredUsername).orElse(null);
            if (usernameOwner == null || usernameOwner.getId().equals(user.getId())) {
                user.setUsername(preferredUsername);
                changed = true;
            }
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            user.setActive(true);
            changed = true;
        }

        return changed;
    }

    private boolean syncRoles(UserEntity user, Collection<String> realmRoles) {
        if (realmRoles == null || realmRoles.isEmpty()) {
            return false;
        }

        Set<String> wanted = new LinkedHashSet<>();
        for (String role : realmRoles) {
            String normalized = role == null ? "" : role.toUpperCase(Locale.ROOT);
            if (MANAGED_ROLES.contains(normalized)) {
                wanted.add(normalized);
            }
        }
        if (wanted.isEmpty()) {
            wanted.add("USER");
        }

        Set<String> current = new HashSet<>();
        if (user.getRoles() != null) {
            for (RoleEntity role : user.getRoles()) {
                current.add(role.getName());
            }
        }
        if (current.equals(wanted)) {
            return false;
        }

        Set<RoleEntity> roles = new HashSet<>();
        for (String name : wanted) {
            roles.add(roleRepository.findByName(name)
                    .orElseGet(() -> roleRepository.save(new RoleEntity(name))));
        }
        user.setRoles(roles);
        return true;
    }
}
