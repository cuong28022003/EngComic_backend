package mobile.apis.reader;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.CreateMistakeBatchRequest;
import mobile.apis.reader.dtos.ToeicMistakeDto;
import mobile.apis.reader.dtos.UpdateMistakeRequest;
import mobile.businesses.boundaries.reader.CreateMistakeBatchBoundary;
import mobile.businesses.boundaries.reader.DeleteToeicMistakeBoundary;
import mobile.businesses.boundaries.reader.GetToeicMistakesBoundary;
import mobile.businesses.boundaries.reader.UpdateToeicMistakeBoundary;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/toeic/mistakes")
@RequiredArgsConstructor
public class ToeicMistakeController {

    private final GetToeicMistakesBoundary getToeicMistakes;
    private final CreateMistakeBatchBoundary createMistakeBatch;
    private final UpdateToeicMistakeBoundary updateToeicMistake;
    private final DeleteToeicMistakeBoundary deleteToeicMistake;
    private final mobile.businesses.boundaries.reader.ImportMistakeReviewsBoundary importMistakeReviews;

    @PostMapping("/import-reviews")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<List<ToeicMistakeDto>> importReviews(
            @CurrentUserId String userId,
            @Valid @RequestBody mobile.apis.reader.dtos.ImportReviewItemsRequest request) {
        var res = importMistakeReviews.execute(
                mobile.businesses.boundaries.reader.ImportMistakeReviewsBoundary.Request.builder()
                        .userId(userId)
                        .importData(request)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<Page<ToeicMistakeDto>> getMistakes(
            @CurrentUserId String userId,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        GetToeicMistakesBoundary.Response res = getToeicMistakes.execute(
                GetToeicMistakesBoundary.Request.builder()
                        .userId(userId)
                        .status(status)
                        .pageable(pageable)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @PostMapping("/batch")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<List<ToeicMistakeDto>> createBatch(
            @CurrentUserId String userId,
            @Valid @RequestBody CreateMistakeBatchRequest batchRequest) {

        CreateMistakeBatchBoundary.Response res = createMistakeBatch.execute(
                CreateMistakeBatchBoundary.Request.builder()
                        .userId(userId)
                        .batchData(batchRequest)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @PatchMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<ToeicMistakeDto> updateMistake(
            @CurrentUserId String userId,
            @PathVariable String id,
            @RequestBody UpdateMistakeRequest updateRequest) {

        UpdateToeicMistakeBoundary.Response res = updateToeicMistake.execute(
                UpdateToeicMistakeBoundary.Request.builder()
                        .userId(userId)
                        .mistakeId(id)
                        .updateData(updateRequest)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<Void> deleteMistake(
            @CurrentUserId String userId,
            @PathVariable String id) {

        deleteToeicMistake.execute(
                DeleteToeicMistakeBoundary.Request.builder()
                        .userId(userId)
                        .mistakeId(id)
                        .build());
        return ResponseEntity.noContent().build();
    }
}
