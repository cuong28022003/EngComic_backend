package mobile.apis.vocab;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CreatePendingItemRequest;
import mobile.apis.vocab.dtos.PendingItemResponseDto;
import mobile.businesses.boundaries.vocab.AddPendingItem;
import mobile.businesses.boundaries.vocab.DeletePendingItem;
import mobile.businesses.boundaries.vocab.GeneratePrompt;
import mobile.businesses.boundaries.vocab.GetPendingItems;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pending-item")
@RequiredArgsConstructor
public class PendingItemController {

    private final AddPendingItem addPendingItem;
    private final GetPendingItems getPendingItems;
    private final DeletePendingItem deletePendingItem;
    private final GeneratePrompt generatePrompt;

    @PostMapping
    @PreAuthorize(AppAuthorities.HAS_PENDING_ITEM_MANAGE)
    public ResponseEntity<PendingItemResponseDto> addPendingItem(
            @CurrentUserId String userId,
            @Valid @RequestBody CreatePendingItemRequest req) {

        AddPendingItem.Request boundaryReq = AddPendingItem.Request.builder()
                .userId(userId)
                .content(req.getContent())
                .sourceType(req.getSourceType())
                .sourceCardId(req.getSourceCardId())
                .build();

        AddPendingItem.Response response = addPendingItem.execute(boundaryReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getItem());
    }

    @GetMapping
    @PreAuthorize(AppAuthorities.HAS_PENDING_ITEM_MANAGE)
    public ResponseEntity<Page<PendingItemResponseDto>> getPendingItems(
            @CurrentUserId String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);

        GetPendingItems.Request boundaryReq = GetPendingItems.Request.builder()
                .userId(userId)
                .status(status)
                .pageable(pageable)
                .build();

        GetPendingItems.Response response = getPendingItems.execute(boundaryReq);
        return ResponseEntity.ok(response.getItems());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_PENDING_ITEM_MANAGE)
    public ResponseEntity<?> deletePendingItem(
            @CurrentUserId String userId,
            @PathVariable String id) {

        DeletePendingItem.Request boundaryReq = DeletePendingItem.Request.builder()
                .id(id)
                .userId(userId)
                .build();

        deletePendingItem.execute(boundaryReq);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/generate-prompt")
    @PreAuthorize(AppAuthorities.HAS_PENDING_ITEM_MANAGE)
    public ResponseEntity<Map<String, String>> generatePrompt(
            @CurrentUserId String userId) {

        GeneratePrompt.Request boundaryReq = GeneratePrompt.Request.builder()
                .userId(userId)
                .build();

        GeneratePrompt.Response response = generatePrompt.execute(boundaryReq);
        return ResponseEntity.ok(Map.of("prompt", response.getPrompt()));
    }

    @PostMapping("/add-manual")
    @PreAuthorize(AppAuthorities.HAS_PENDING_ITEM_MANAGE)
    public ResponseEntity<PendingItemResponseDto> addManual(
            @CurrentUserId String userId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AddPendingItem.Request boundaryReq = AddPendingItem.Request.builder()
                .userId(userId)
                .content(content.trim())
                .sourceType("manual")
                .sourceCardId(null)
                .build();

        AddPendingItem.Response response = addPendingItem.execute(boundaryReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getItem());
    }
}

