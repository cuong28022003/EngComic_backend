package mobile.apis.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserStatsResponseDto;
import mobile.businesses.boundaries.user.GetUserStats;
import mobile.businesses.boundaries.user.RecordStudyActivity;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/userstats", "/api/user-stats"})
@RequiredArgsConstructor
public class UserStatsController {

    private final GetUserStats getUserStats;
    private final RecordStudyActivity recordStudyActivity;

    @GetMapping("/me")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<UserStatsResponseDto> getMyStats(@CurrentUserId String currentUserId) {
        GetUserStats.Request request = GetUserStats.Request.builder()
                .userId(currentUserId)
                .build();

        GetUserStats.Response response = getUserStats.execute(request);
        return ResponseEntity.ok(response.getStats());
    }

    @GetMapping("/{userId}")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<UserStatsResponseDto> getUserStats(
            @CurrentUserId String currentUserId,
            @PathVariable String userId) {
        String targetUserId = (userId != null && !userId.isBlank()) ? userId : currentUserId;
        GetUserStats.Request request = GetUserStats.Request.builder()
                .userId(targetUserId)
                .build();

        GetUserStats.Response response = getUserStats.execute(request);
        return ResponseEntity.ok(response.getStats());
    }

    @PostMapping({"/check-in", "/record-activity"})
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<RecordStudyActivity.Response> recordActivity(
            @CurrentUserId String currentUserId,
            @RequestBody(required = false) Map<String, Object> payload) {
        int xpEarned = 10;
        String activityType = "practice";
        if (payload != null) {
            if (payload.containsKey("xp")) {
                try {
                    xpEarned = Integer.parseInt(payload.get("xp").toString());
                } catch (Exception ignored) {}
            }
            if (payload.containsKey("activityType")) {
                activityType = payload.get("activityType").toString();
            }
        }

        RecordStudyActivity.Request req = RecordStudyActivity.Request.builder()
                .userId(currentUserId)
                .xpEarned(xpEarned)
                .activityType(activityType)
                .build();

        RecordStudyActivity.Response res = recordStudyActivity.execute(req);
        return ResponseEntity.ok(res);
    }
}
