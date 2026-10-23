package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.user.CheckEmail;
import mobile.databases.repositories.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckEmailInteractor implements CheckEmail {

    private final UserRepository userRepository;

    @Override
    public Response execute(Request request) {
        String email = request.getEmail();
        boolean valid = email != null
                && !email.isBlank()
                && !Boolean.TRUE.equals(userRepository.existsByEmail(email));

        return Response.builder()
                .valid(valid)
                .build();
    }
}
