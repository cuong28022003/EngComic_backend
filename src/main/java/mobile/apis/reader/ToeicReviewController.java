package mobile.apis.reader;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.reader.dtos.ImportReviewItemsRequest;
import mobile.apis.reader.dtos.ToeicReviewItemDto;
import mobile.businesses.boundaries.reader.GetAttemptReviewsBoundary;
import mobile.businesses.boundaries.reader.ImportReviewItemsBoundary;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/toeic/attempts/{attemptId}/reviews")
@RequiredArgsConstructor
public class ToeicReviewController {

    private final ImportReviewItemsBoundary importReviewItems;
    private final GetAttemptReviewsBoundary getAttemptReviews;

    @PostMapping("/import")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<List<ToeicReviewItemDto>> importReviews(
            @CurrentUserId String userId,
            @PathVariable String attemptId,
            @Valid @RequestBody ImportReviewItemsRequest request) {
        ImportReviewItemsBoundary.Response res = importReviewItems.execute(
                ImportReviewItemsBoundary.Request.builder()
                        .userId(userId)
                        .attemptId(attemptId)
                        .importData(request)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<List<ToeicReviewItemDto>> getReviews(
            @CurrentUserId String userId,
            @PathVariable String attemptId) {
        GetAttemptReviewsBoundary.Response res = getAttemptReviews.execute(
                GetAttemptReviewsBoundary.Request.builder()
                        .userId(userId)
                        .attemptId(attemptId)
                        .build());
        return ResponseEntity.ok(res.getData());
    }
}
