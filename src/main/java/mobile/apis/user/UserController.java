package mobile.apis.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.user.GetUserProfile;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final GetUserProfile getUserProfile;

    @GetMapping("/me")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(@CurrentUserId String currentUserId) {
        GetUserProfile.Request request = GetUserProfile.Request.builder()
                .userId(currentUserId)
                .build();

        GetUserProfile.Response response = getUserProfile.execute(request);
        return ResponseEntity.ok(response.getProfile());
    }

    @GetMapping("/{userId}")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<UserProfileDto> getUserProfileById(@PathVariable String userId) {
        GetUserProfile.Request request = GetUserProfile.Request.builder()
                .userId(userId)
                .build();

        GetUserProfile.Response response = getUserProfile.execute(request);
        return ResponseEntity.ok(response.getProfile());
    }
}
