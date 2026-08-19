package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.RegisterRequestDto;
import mobile.businesses.boundaries.user.RegisterUser;
import mobile.databases.entities.user.RoleEntity;
import mobile.databases.entities.user.UserEntity;
import mobile.databases.repositories.user.RoleRepository;
import mobile.databases.repositories.user.UserRepository;
import mobile.domains.user.UserRules;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterUserInteractor implements RegisterUser {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Response execute(Request request) {
        RegisterRequestDto payload = request.getPayload();
        if (payload == null) {
            throw new IllegalArgumentException("Payload is required");
        }

        if (!UserRules.isValidEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!UserRules.isValidUsername(payload.getUsername())) {
            throw new IllegalArgumentException("Username must be 3-50 characters");
        }
        if (!UserRules.isValidPassword(payload.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Email already taken: " + payload.getEmail());
        }
        if (userRepository.existsByUsername(payload.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + payload.getUsername());
        }

        RoleEntity defaultRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new RoleEntity("USER")));

        Set<RoleEntity> roles = new HashSet<>(Collections.singletonList(defaultRole));

        UserEntity newUser = UserEntity.builder()
                .username(payload.getUsername())
                .email(payload.getEmail())
                .password(passwordEncoder.encode(payload.getPassword()))
                .fullName(payload.getUsername())
                .active(true)
                .status("ACTIVE")
                .roles(roles)
                .build();

        userRepository.save(newUser);

        return Response.builder()
                .email(newUser.getEmail())
                .message("Register successful")
                .build();
    }
}

