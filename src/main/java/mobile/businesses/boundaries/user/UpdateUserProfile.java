package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.user.dtos.UserProfileDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface UpdateUserProfile {
    Response execute(Request request);

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class Request {
        private String currentUserId;
        private String targetUserId;
        private String fullName;
        private LocalDate birthday;
        private MultipartFile image;
        private MultipartFile cover;
        private String imageUrl;
        private String coverUrl;
        private List<String> featuredCharacterIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class Response {
        private UserProfileDto profile;
    }
}
