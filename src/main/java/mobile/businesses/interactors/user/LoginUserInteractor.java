package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.AuthResponseDto;
import mobile.apis.user.dtos.LoginRequestDto;
import mobile.businesses.boundaries.user.LoginUser;
import mobile.databases.entities.user.UserEntity;
import mobile.databases.repositories.user.UserRepository;
import mobile.security.core.AppUserDetail;
import mobile.security.core.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserInteractor implements LoginUser {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public Response execute(Request request) {
        LoginRequestDto payload = request.getPayload();
        if (payload == null) {
            throw new IllegalArgumentException("Payload is required");
        }

        UserEntity user = userRepository.findByUsername(payload.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (user.getActive() != null && !user.getActive()) {
            throw new BadCredentialsException("Account is not active");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDetail userDetails = (AppUserDetail) authentication.getPrincipal();

        String accessToken = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshJwtToken(userDetails);

        AuthResponseDto responseDto = AuthResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .imageUrl(user.getImage())
                .birthday(user.getBirthday())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(userDetails.getRoles())
                .build();

        return Response.builder()
                .data(responseDto)
                .build();
    }
}

