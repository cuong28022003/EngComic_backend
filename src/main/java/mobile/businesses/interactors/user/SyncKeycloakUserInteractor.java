package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.user.SyncKeycloakUser;
import mobile.databases.entities.user.UserEntity;
import mobile.databases.repositories.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncKeycloakUserInteractor implements SyncKeycloakUser {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        UserProfileDto profile = userMapper.toProfileDto(user);
        return Response.builder()
                .data(profile)
                .build();
    }
}
