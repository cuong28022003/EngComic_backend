package mobile.apis.grammar;

import lombok.RequiredArgsConstructor;
import mobile.apis.grammar.dtos.BatchImportGrammarRequest;
import mobile.apis.grammar.dtos.CreateOrUpdateGrammarPointRequest;
import mobile.apis.grammar.dtos.GrammarPointDto;
import mobile.apis.grammar.dtos.MostMissedGrammarDto;
import mobile.businesses.boundaries.grammar.BatchImportGrammarBoundary;
import mobile.businesses.boundaries.grammar.DeleteGrammarPointBoundary;
import mobile.businesses.boundaries.grammar.GetGrammarPointDetailBoundary;
import mobile.businesses.boundaries.grammar.GetGrammarPointsBoundary;
import mobile.businesses.boundaries.grammar.GetMostMissedGrammarBoundary;
import mobile.businesses.boundaries.grammar.SaveGrammarPointBoundary;
import mobile.security.core.AppUserDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grammar")
@RequiredArgsConstructor
public class GrammarController {

    private final GetGrammarPointsBoundary getGrammarPointsBoundary;
    private final GetGrammarPointDetailBoundary getGrammarPointDetailBoundary;
    private final SaveGrammarPointBoundary saveGrammarPointBoundary;
    private final DeleteGrammarPointBoundary deleteGrammarPointBoundary;
    private final BatchImportGrammarBoundary batchImportGrammarBoundary;
    private final GetMostMissedGrammarBoundary getMostMissedGrammarBoundary;

    @GetMapping
    public ResponseEntity<List<GrammarPointDto>> getGrammarPoints(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        GetGrammarPointsBoundary.Request request = GetGrammarPointsBoundary.Request.builder()
                .category(category)
                .keyword(keyword)
                .build();

        GetGrammarPointsBoundary.Response response = getGrammarPointsBoundary.execute(request);
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrammarPointDto> getGrammarPointById(@PathVariable String id) {
        GetGrammarPointDetailBoundary.Request request = GetGrammarPointDetailBoundary.Request.builder()
                .id(id)
                .build();

        GetGrammarPointDetailBoundary.Response response = getGrammarPointDetailBoundary.execute(request);
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/by-topic")
    public ResponseEntity<GrammarPointDto> getGrammarPointByTopic(@RequestParam String topic) {
        GetGrammarPointDetailBoundary.Request request = GetGrammarPointDetailBoundary.Request.builder()
                .topic(topic)
                .build();

        GetGrammarPointDetailBoundary.Response response = getGrammarPointDetailBoundary.execute(request);
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/most-missed")
    public ResponseEntity<List<MostMissedGrammarDto>> getMostMissedGrammar(
            @AuthenticationPrincipal AppUserDetail userDetail,
            @RequestParam(defaultValue = "5") int limit
    ) {
        String userId = userDetail != null ? userDetail.getId() : null;

        GetMostMissedGrammarBoundary.Request request = GetMostMissedGrammarBoundary.Request.builder()
                .userId(userId)
                .limit(limit)
                .build();

        GetMostMissedGrammarBoundary.Response response = getMostMissedGrammarBoundary.execute(request);
        return ResponseEntity.ok(response.getData());
    }

    @PostMapping
    public ResponseEntity<GrammarPointDto> createGrammarPoint(
            @RequestBody CreateOrUpdateGrammarPointRequest requestDto
    ) {
        SaveGrammarPointBoundary.Request request = SaveGrammarPointBoundary.Request.builder()
                .id(null)
                .data(requestDto)
                .build();

        SaveGrammarPointBoundary.Response response = saveGrammarPointBoundary.execute(request);
        return ResponseEntity.ok(response.getData());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrammarPointDto> updateGrammarPoint(
            @PathVariable String id,
            @RequestBody CreateOrUpdateGrammarPointRequest requestDto
    ) {
        SaveGrammarPointBoundary.Request request = SaveGrammarPointBoundary.Request.builder()
                .id(id)
                .data(requestDto)
                .build();

        SaveGrammarPointBoundary.Response response = saveGrammarPointBoundary.execute(request);
        return ResponseEntity.ok(response.getData());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteGrammarPoint(@PathVariable String id) {
        DeleteGrammarPointBoundary.Request request = DeleteGrammarPointBoundary.Request.builder()
                .id(id)
                .build();

        DeleteGrammarPointBoundary.Response response = deleteGrammarPointBoundary.execute(request);
        return ResponseEntity.ok(Map.of(
                "success", response.isSuccess(),
                "message", response.getMessage()
        ));
    }

    @PostMapping("/batch-import")
    public ResponseEntity<BatchImportGrammarBoundary.Response> batchImportGrammar(
            @RequestBody BatchImportGrammarRequest requestDto
    ) {
        BatchImportGrammarBoundary.Request request = BatchImportGrammarBoundary.Request.builder()
                .items(requestDto.getItems())
                .build();

        BatchImportGrammarBoundary.Response response = batchImportGrammarBoundary.execute(request);
        return ResponseEntity.ok(response);
    }
}
