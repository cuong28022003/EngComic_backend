package mobile.apis.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserStatsResponseDto;
import mobile.businesses.boundaries.user.GetLearningStats;
import mobile.businesses.boundaries.user.RecordStudyActivity;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/learning-stats", "/api/learningstats", "/api/userstats"})
@RequiredArgsConstructor
public class LearningStatsController {

    private final GetLearningStats getLearningStats;
    private final RecordStudyActivity recordStudyActivity;
    private final mobile.businesses.boundaries.user.EquipPrestigeItem equipPrestigeItem;

    @GetMapping("/me")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<UserStatsResponseDto> getMyLearningStats(@CurrentUserId String currentUserId) {
        GetLearningStats.Request request = GetLearningStats.Request.builder()
                .userId(currentUserId)
                .build();

        GetLearningStats.Response response = getLearningStats.execute(request);
        return ResponseEntity.ok(response.getStats());
    }

    @GetMapping("/{userId}")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<UserStatsResponseDto> getUserLearningStats(
            @CurrentUserId String currentUserId,
            @PathVariable String userId) {
        String targetUserId = (userId != null && !userId.isBlank()) ? userId : currentUserId;
        GetLearningStats.Request request = GetLearningStats.Request.builder()
                .userId(targetUserId)
                .build();

        GetLearningStats.Response response = getLearningStats.execute(request);
        return ResponseEntity.ok(response.getStats());
    }

    @PostMapping("/record-activity")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<RecordStudyActivity.Response> recordLearningActivity(
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

    @PostMapping("/equip")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<mobile.businesses.boundaries.user.EquipPrestigeItem.Response> equipPrestige(
            @CurrentUserId String currentUserId,
            @RequestBody Map<String, String> payload) {
        String itemType = payload.getOrDefault("itemType", "title");
        String itemId = payload.getOrDefault("itemId", "");

        mobile.businesses.boundaries.user.EquipPrestigeItem.Request req =
                mobile.businesses.boundaries.user.EquipPrestigeItem.Request.builder()
                        .userId(currentUserId)
                        .itemType(itemType)
                        .itemId(itemId)
                        .build();

        mobile.businesses.boundaries.user.EquipPrestigeItem.Response res = equipPrestigeItem.execute(req);
        return ResponseEntity.ok(res);
    }
}
