package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.user.RefreshToken;
import mobile.databases.entities.user.UserEntity;
import mobile.databases.repositories.user.UserRepository;
import mobile.security.core.AppUserDetail;
import mobile.security.core.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenInteractor implements RefreshToken {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public Response execute(Request request) {
        String token = request.getRefreshToken();
        if (token == null || token.isBlank() || !jwtUtils.validateJwtToken(token)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BadCredentialsException("Account is not active");
        }

        AppUserDetail userDetails = AppUserDetail.build(user);
        String accessToken = jwtUtils.generateJwtToken(userDetails);

        return Response.builder()
                .accessToken(accessToken)
                .build();
    }
}
