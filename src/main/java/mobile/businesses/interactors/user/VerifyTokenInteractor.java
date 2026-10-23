package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.user.VerifyToken;
import mobile.databases.entities.user.UserEntity;
import mobile.databases.repositories.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyTokenInteractor implements VerifyToken {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            return Response.builder().valid(false).build();
        }

        return userRepository.findById(request.getUserId())
                .filter(this::isActive)
                .map(user -> Response.builder()
                        .valid(true)
                        .user(userMapper.toProfileDto(user))
                        .build())
                .orElseGet(() -> Response.builder().valid(false).build());
    }

    private boolean isActive(UserEntity user) {
        return !Boolean.FALSE.equals(user.getActive());
    }
}
