package mobile.apis.userstats;

import lombok.RequiredArgsConstructor;
import mobile.apis.userstats.dtos.UserStatsResponseDto;
import mobile.businesses.boundaries.userstats.GetUserStats;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/userstats")
@RequiredArgsConstructor
public class UserStatsController {

    private final GetUserStats getUserStats;

    @GetMapping("/me")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<UserStatsResponseDto> getMyStats(@CurrentUserId String currentUserId) {
        GetUserStats.Request request = GetUserStats.Request.builder()
                .userId(currentUserId)
                .build();

        GetUserStats.Response response = getUserStats.execute(request);
        return ResponseEntity.ok(response.getStats());
    }
}
