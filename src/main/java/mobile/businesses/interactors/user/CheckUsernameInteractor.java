package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.user.CheckUsername;
import mobile.databases.repositories.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckUsernameInteractor implements CheckUsername {

    private final UserRepository userRepository;

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        boolean valid = username != null
                && !username.isBlank()
                && !Boolean.TRUE.equals(userRepository.existsByUsername(username));

        return Response.builder()
                .valid(valid)
                .build();
    }
}
