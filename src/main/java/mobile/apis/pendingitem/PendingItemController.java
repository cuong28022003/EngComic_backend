package mobile.apis.pendingitem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile.apis.pendingitem.dtos.CreatePendingItemRequest;
import mobile.apis.pendingitem.dtos.PendingItemResponseDto;
import mobile.businesses.boundaries.pendingitem.AddPendingItem;
import mobile.businesses.boundaries.pendingitem.DeletePendingItem;
import mobile.businesses.boundaries.pendingitem.GeneratePrompt;
import mobile.businesses.boundaries.pendingitem.GetPendingItems;
import mobile.security.SecurityUtils;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PendingItemResponseDto> addPendingItem(@Valid @RequestBody CreatePendingItemRequest req) {
        String userId = SecurityUtils.getCurrentUserId();

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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PendingItemResponseDto>> getPendingItems(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = SecurityUtils.getCurrentUserId();
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePendingItem(@PathVariable String id) {
        String userId = SecurityUtils.getCurrentUserId();

        DeletePendingItem.Request boundaryReq = DeletePendingItem.Request.builder()
                .id(id)
                .userId(userId)
                .build();

        deletePendingItem.execute(boundaryReq);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/generate-prompt")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> generatePrompt() {
        String userId = SecurityUtils.getCurrentUserId();

        GeneratePrompt.Request boundaryReq = GeneratePrompt.Request.builder()
                .userId(userId)
                .build();

        GeneratePrompt.Response response = generatePrompt.execute(boundaryReq);
        return ResponseEntity.ok(Map.of("prompt", response.getPrompt()));
    }

    @PostMapping("/add-manual")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PendingItemResponseDto> addManual(@RequestBody Map<String, String> body) {
        String userId = SecurityUtils.getCurrentUserId();
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
