package mobile.apis.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.user.GetUserProfile;
import mobile.businesses.boundaries.user.UpdateUserProfile;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final GetUserProfile getUserProfile;
    private final UpdateUserProfile updateUserProfile;

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

    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(AppAuthorities.HAS_USER_WRITE)
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @CurrentUserId String currentUserId,
            @PathVariable String userId,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "birthday", required = false) String birthday,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "coverUrl", required = false) String coverUrl) {

        LocalDate parsedBirthday = null;
        if (birthday != null && !birthday.isBlank()) {
            try {
                parsedBirthday = LocalDate.parse(birthday);
            } catch (Exception ignored) {
            }
        }

        UpdateUserProfile.Request request = UpdateUserProfile.Request.builder()
                .currentUserId(currentUserId)
                .targetUserId(userId)
                .fullName(fullName)
                .birthday(parsedBirthday)
                .image(image)
                .cover(cover)
                .imageUrl(imageUrl)
                .coverUrl(coverUrl)
                .build();

        UpdateUserProfile.Response response = updateUserProfile.execute(request);
        return ResponseEntity.ok(response.getProfile());
    }

    @PutMapping(value = "/{userId}/featured-characters", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AppAuthorities.HAS_USER_WRITE)
    public ResponseEntity<UserProfileDto> updateFeaturedCharacters(
            @CurrentUserId String currentUserId,
            @PathVariable String userId,
            @RequestBody FeaturedCharactersRequest body) {

        UpdateUserProfile.Request request = UpdateUserProfile.Request.builder()
                .currentUserId(currentUserId)
                .targetUserId(userId)
                .featuredCharacterIds(body.getFeaturedCharacterIds())
                .build();

        UpdateUserProfile.Response response = updateUserProfile.execute(request);
        return ResponseEntity.ok(response.getProfile());
    }

    public static class FeaturedCharactersRequest {
        private List<String> featuredCharacterIds;

        public List<String> getFeaturedCharacterIds() {
            return featuredCharacterIds;
        }

        public void setFeaturedCharacterIds(List<String> featuredCharacterIds) {
            this.featuredCharacterIds = featuredCharacterIds;
        }
    }
}
