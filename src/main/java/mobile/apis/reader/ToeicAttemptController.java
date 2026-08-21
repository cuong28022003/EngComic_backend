package mobile.apis.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.reader.dtos.*;
import mobile.businesses.boundaries.reader.*;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ToeicAttemptController {

    private final StartTestAttemptBoundary startTestAttempt;
    private final SaveAttemptProgressBoundary saveAttemptProgress;
    private final AbandonAttemptBoundary abandonAttempt;
    private final GetActiveAttemptBoundary getActiveAttempt;
    private final GetTestAttemptsBoundary getTestAttempts;
    private final GetAttemptDetailBoundary getAttemptDetail;
    private final SubmitToeicSessionBoundary submitToeicSession;
    private final ObjectMapper objectMapper;

    @PostMapping("/api/toeic/tests/{testId}/attempts")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<ToeicAttemptDto> startAttempt(
            @CurrentUserId String userId,
            @PathVariable String testId,
            @RequestBody(required = false) StartAttemptRequest request) {
        StartTestAttemptBoundary.Response res = startTestAttempt.execute(
                StartTestAttemptBoundary.Request.builder()
                        .userId(userId)
                        .testId(testId)
                        .startData(request)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping("/api/toeic/tests/{testId}/attempts")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<List<ToeicAttemptDto>> getAttemptsForTest(
            @CurrentUserId String userId,
            @PathVariable String testId) {
        GetTestAttemptsBoundary.Response res = getTestAttempts.execute(
                GetTestAttemptsBoundary.Request.builder()
                        .userId(userId)
                        .testId(testId)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping("/api/toeic/attempts/active")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<ToeicAttemptDto> getActive(
            @CurrentUserId String userId,
            @RequestParam(required = false) String testId) {
        GetActiveAttemptBoundary.Response res = getActiveAttempt.execute(
                GetActiveAttemptBoundary.Request.builder()
                        .userId(userId)
                        .testId(testId)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping("/api/toeic/attempts/{id}")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<ToeicAttemptDto> getAttemptDetail(
            @CurrentUserId String userId,
            @PathVariable String id) {
        GetAttemptDetailBoundary.Response res = getAttemptDetail.execute(
                GetAttemptDetailBoundary.Request.builder()
                        .userId(userId)
                        .attemptId(id)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @PatchMapping("/api/toeic/attempts/{id}/progress")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<ToeicAttemptDto> saveProgress(
            @CurrentUserId String userId,
            @PathVariable String id,
            @RequestBody SaveProgressRequest request) {
        SaveAttemptProgressBoundary.Response res = saveAttemptProgress.execute(
                SaveAttemptProgressBoundary.Request.builder()
                        .userId(userId)
                        .attemptId(id)
                        .progressData(request)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    /**
     * Endpoint hỗ trợ navigator.sendBeacon (chấp nhận cả text/plain hoặc application/json)
     */
    @PostMapping(value = "/api/toeic/attempts/{id}/save-progress")
    public ResponseEntity<Void> saveProgressBeacon(
            @PathVariable String id,
            @RequestBody(required = false) String rawBody,
            @CurrentUserId(required = false) String userId) {
        try {
            if (rawBody != null && !rawBody.trim().isEmpty()) {
                SaveProgressRequest prog = objectMapper.readValue(rawBody, SaveProgressRequest.class);
                if (userId != null) {
                    saveAttemptProgress.execute(
                            SaveAttemptProgressBoundary.Request.builder()
                                    .userId(userId)
                                    .attemptId(id)
                                    .progressData(prog)
                                    .build());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse beacon save-progress payload for attempt {}", id, e);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/toeic/attempts/{id}/abandon")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<ToeicAttemptDto> abandon(
            @CurrentUserId String userId,
            @PathVariable String id) {
        AbandonAttemptBoundary.Response res = abandonAttempt.execute(
                AbandonAttemptBoundary.Request.builder()
                        .userId(userId)
                        .attemptId(id)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @PostMapping("/api/toeic/attempts/{id}/submit")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<SubmitToeicSessionResponse> submitAttempt(
            @CurrentUserId String userId,
            @PathVariable String id,
            @RequestBody SubmitToeicSessionRequest request) {
        GetAttemptDetailBoundary.Response attemptRes = getAttemptDetail.execute(
                GetAttemptDetailBoundary.Request.builder()
                        .userId(userId)
                        .attemptId(id)
                        .build());
        String testId = attemptRes.getData().getTestId();
        request.setAttemptId(id);

        SubmitToeicSessionBoundary.Response res = submitToeicSession.execute(
                SubmitToeicSessionBoundary.Request.builder()
                        .userId(userId)
                        .testId(testId)
                        .submissionData(request)
                        .build());
        return ResponseEntity.ok(res.getData());
    }
}
