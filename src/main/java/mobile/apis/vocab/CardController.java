package mobile.apis.vocab;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.BatchImportRequest;
import mobile.apis.vocab.dtos.BatchImportResponseDto;
import mobile.apis.vocab.dtos.CardDetailResponseDto;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.apis.vocab.dtos.CreateCardRequest;
import mobile.apis.vocab.dtos.DashboardResponseDto;
import mobile.apis.vocab.dtos.PracticeResultRequest;
import mobile.businesses.boundaries.vocab.*;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {

    private final GetUserCardsBoundary getUserCardsBoundary;
    private final CreateCardBoundary createCardBoundary;
    private final BatchImportCard batchImportCard;
    private final GetCardDashboard getCardDashboard;
    private final GetCardDetail getCardDetail;
    private final GetDuePracticeCards getDuePracticeCards;
    private final SubmitPracticeResult submitPracticeResult;
    private final UpdateCard updateCard;

    // Feature 002: Word Journey Practice Boundaries
    private final GeneratePracticePromptBoundary generatePracticePromptBoundary;
    private final ImportPracticeJsonBoundary importPracticeJsonBoundary;
    private final GetPracticeQueueBoundary getPracticeQueueBoundary;
    private final SubmitLevelAnswerBoundary submitLevelAnswerBoundary;
    private final GetLeechCardsBoundary getLeechCardsBoundary;
    private final ClearLeechStatusBoundary clearLeechStatusBoundary;

    // Feature Deck Integration
    private final mobile.businesses.boundaries.vocab.BatchAssignDeckBoundary batchAssignDeckBoundary;
    private final GetUserTopicsBoundary getUserTopicsBoundary;

    private final CardRepository cardRepository;
    private final mobile.businesses.interactors.vocab.CardMapper cardMapper;

    @PostMapping("/batch-import")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<BatchImportResponseDto> batchImport(
            @CurrentUserId String userId,
            @Valid @RequestBody BatchImportRequest batchRequest) {

        BatchImportCard.Request req = BatchImportCard.Request.builder()
                .userId(userId)
                .jsonContent(batchRequest.getJsonContent())
                .deckId(batchRequest.getDeckId())
                .build();

        BatchImportCard.Response response = batchImportCard.execute(req);
        return ResponseEntity.ok(response.getData());
    }

    @PostMapping("/batch-assign-deck")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<java.util.Map<String, Object>> batchAssignDeck(
            @CurrentUserId String userId,
            @Valid @RequestBody mobile.apis.vocab.dtos.BatchAssignDeckRequest request) {

        mobile.businesses.boundaries.vocab.BatchAssignDeckBoundary.Request req =
                mobile.businesses.boundaries.vocab.BatchAssignDeckBoundary.Request.builder()
                        .userId(userId)
                        .cardIds(request.getCardIds())
                        .deckId(request.getDeckId())
                        .build();

        mobile.businesses.boundaries.vocab.BatchAssignDeckBoundary.Response response = batchAssignDeckBoundary.execute(req);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("totalAssigned", response.getTotalAssigned());
        body.put("message", response.getMessage());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/topics")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<List<String>> getUserTopics(@CurrentUserId String userId) {
        List<String> topics = getUserTopicsBoundary.execute(userId);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/dashboard")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<DashboardResponseDto> getDashboard(
            @CurrentUserId String userId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String deckId,
            @RequestParam(required = false) String partOfSpeech,
            @RequestParam(required = false) String usageCategory,
            @RequestParam(required = false) Boolean isFavorite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);

        GetCardDashboard.Request req = GetCardDashboard.Request.builder()
                .userId(userId)
                .search(search)
                .status(status)
                .topic(topic)
                .deckId(deckId)
                .partOfSpeech(partOfSpeech)
                .usageCategory(usageCategory)
                .isFavorite(isFavorite)
                .pageable(pageable)
                .build();

        GetCardDashboard.Response response = getCardDashboard.execute(req);
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<CardDetailResponseDto> getCardById(
            @CurrentUserId String userId,
            @PathVariable String id) {

        GetCardDetail.Request req = GetCardDetail.Request.builder()
                .cardId(id)
                .userId(userId)
                .build();

        GetCardDetail.Response response = getCardDetail.execute(req);
        if (response != null && response.getData() != null && response.getData().getCard() != null) {
            return ResponseEntity.ok(response.getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/practice/due")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<List<CardResponseDto>> getDueCards(
            @CurrentUserId String userId,
            @RequestParam(defaultValue = "15") int limit) {

        GetDuePracticeCards.Request req = GetDuePracticeCards.Request.builder()
                .userId(userId)
                .limit(limit)
                .build();

        GetDuePracticeCards.Response response = getDuePracticeCards.execute(req);
        return ResponseEntity.ok(response.getCards());
    }

    @PostMapping("/{id}/practice-result")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<CardResponseDto> submitPracticeResult(
            @PathVariable String id,
            @Valid @RequestBody PracticeResultRequest resultRequest) {
        SubmitPracticeResult.Request req = SubmitPracticeResult.Request.builder()
                .cardId(id)
                .quality(resultRequest.getQuality())
                .build();

        SubmitPracticeResult.Response response = submitPracticeResult.execute(req);
        if (response != null && response.getCard() != null) {
            return ResponseEntity.ok(response.getCard());
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // FEATURE 002: WORD JOURNEY PRACTICE APIS
    // ==========================================

    @GetMapping({"/practice-prompt", "/deck/{deckId}/practice-prompt"})
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<mobile.apis.vocab.dtos.PracticePromptResponseDto> getPracticePrompt(
            @CurrentUserId String userId,
            @PathVariable(required = false) String deckId,
            @RequestParam(required = false) String deck) {
        String targetDeckId = (deckId != null && !deckId.isBlank()) ? deckId : deck;
        GeneratePracticePromptBoundary.Request req = GeneratePracticePromptBoundary.Request.builder()
                .userId(userId)
                .deckId(targetDeckId)
                .build();

        GeneratePracticePromptBoundary.Response res = generatePracticePromptBoundary.execute(req);
        return ResponseEntity.ok(res.getData());
    }

    @PostMapping({"/import-practice-json", "/deck/{deckId}/import-practice-json"})
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<java.util.Map<String, Object>> importPracticeJson(
            @CurrentUserId String userId,
            @PathVariable(required = false) String deckId,
            @RequestParam(required = false) String deck,
            @Valid @RequestBody mobile.apis.vocab.dtos.ImportPracticeJsonRequest request) {
        String targetDeckId = (deckId != null && !deckId.isBlank()) ? deckId : deck;
        ImportPracticeJsonBoundary.Request req = ImportPracticeJsonBoundary.Request.builder()
                .userId(userId)
                .deckId(targetDeckId)
                .jsonContent(request.getJsonContent())
                .build();

        ImportPracticeJsonBoundary.Response res = importPracticeJsonBoundary.execute(req);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("totalProcessed", res.getTotalProcessed());
        body.put("successCount", res.getSuccessCount());
        body.put("message", res.getMessage());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/practice/queue")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<java.util.Map<String, Object>> getPracticeQueue(
            @CurrentUserId String userId,
            @RequestParam(required = false) String deckId,
            @RequestParam(defaultValue = "20") int limit) {
        GetPracticeQueueBoundary.Request req = GetPracticeQueueBoundary.Request.builder()
                .userId(userId)
                .deckId(deckId)
                .limit(limit)
                .build();

        GetPracticeQueueBoundary.Response res = getPracticeQueueBoundary.execute(req);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("items", res.getItems());
        body.put("totalDue", res.getTotalDue());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/submit-level")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<mobile.apis.vocab.dtos.SubmitLevelAnswerResponseDto> submitLevelAnswer(
            @CurrentUserId String userId,
            @PathVariable String id,
            @Valid @RequestBody mobile.apis.vocab.dtos.SubmitLevelAnswerRequest payload) {
        SubmitLevelAnswerBoundary.Request req = SubmitLevelAnswerBoundary.Request.builder()
                .userId(userId)
                .cardId(id)
                .payload(payload)
                .build();

        SubmitLevelAnswerBoundary.Response res = submitLevelAnswerBoundary.execute(req);
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping("/leech")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<List<CardResponseDto>> getLeechCards(
            @CurrentUserId String userId) {
        GetLeechCardsBoundary.Request req = GetLeechCardsBoundary.Request.builder()
                .userId(userId)
                .build();

        GetLeechCardsBoundary.Response res = getLeechCardsBoundary.execute(req);
        return ResponseEntity.ok(res.getCards());
    }

    @PostMapping("/{id}/clear-leech")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<java.util.Map<String, Object>> clearLeechStatus(
            @CurrentUserId String userId,
            @PathVariable String id,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        String memoryTip = body != null ? body.get("memoryTip") : null;
        ClearLeechStatusBoundary.Request req = ClearLeechStatusBoundary.Request.builder()
                .userId(userId)
                .cardId(id)
                .memoryTip(memoryTip)
                .build();

        ClearLeechStatusBoundary.Response res = clearLeechStatusBoundary.execute(req);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("card", res.getCard());
        response.put("message", res.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deck/{deckId}")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<Page<CardResponseDto>> getCardsByDeckId(
            @PathVariable String deckId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardEntity> card = cardRepository.findByDeckId(deckId, pageable);
        Page<CardResponseDto> cardResponsePage = card.map(cardMapper::toResponse);
        return ResponseEntity.ok(cardResponsePage);
    }

    @GetMapping("/deck/{deckId}/due")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<Page<CardResponseDto>> getDueCardsByDeckId(
            @PathVariable String deckId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardEntity> card = cardRepository.findByDeckIdAndNextReviewLessThanEqual(deckId, new java.util.Date(), pageable);
        Page<CardResponseDto> cardResponsePage = card.map(cardMapper::toResponse);
        return ResponseEntity.ok(cardResponsePage);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<Page<CardResponseDto>> getCardsByUserId(
            @PathVariable String userId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        GetUserCardsBoundary.Request boundaryRequest = new GetUserCardsBoundary.Request(userId, search, pageable);
        GetUserCardsBoundary.Response response = getUserCardsBoundary.execute(boundaryRequest);
        return ResponseEntity.ok(response.getCards());
    }

    @PostMapping()
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<CardResponseDto> createCard(
            @CurrentUserId String currentUserId,
            @RequestBody CreateCardRequest createCardRequest) {

        CreateCardBoundary.Request boundaryRequest = new CreateCardBoundary.Request(createCardRequest, currentUserId);
        CreateCardBoundary.Response response = createCardBoundary.execute(boundaryRequest);
        return ResponseEntity.ok(response.getCard());
    }

    @PutMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<CardResponseDto> updateCard(
            @PathVariable String id,
            @RequestBody CreateCardRequest createCardRequest) {
        UpdateCard.Request req = UpdateCard.Request.builder()
                .cardId(id)
                .payload(createCardRequest)
                .build();

        UpdateCard.Response response = updateCard.execute(req);
        if (response != null && response.getCard() != null) {
            return ResponseEntity.ok(response.getCard());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<Void> deleteCard(@PathVariable String id) {
        cardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<List<CardResponseDto>> createCardsBatch(
            @CurrentUserId String currentUserId,
            @RequestBody List<CreateCardRequest> createCardRequests) {

        List<CardResponseDto> responses = createCardRequests.stream().map(req -> {
            CardEntity cardEntity = cardMapper.toEntity(req);
            if (cardEntity.getUserId() == null && currentUserId != null) {
                cardEntity.setUserId(currentUserId);
            }
            CardEntity saved = cardRepository.save(cardEntity);
            return cardMapper.toResponse(saved);
        }).toList();

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/toggle-favorite")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<CardResponseDto> toggleFavorite(@PathVariable String id) {
        CardEntity card = cardRepository.findById(id).orElse(null);
        if (card != null) {
            card.setFavorite(!card.isFavorite());
            CardEntity saved = cardRepository.save(card);
            return ResponseEntity.ok(cardMapper.toResponse(saved));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/move-deck")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<CardResponseDto> moveDeck(@PathVariable String id, @RequestParam String newDeckId) {
        CardEntity card = cardRepository.findById(id).orElse(null);
        if (card != null) {
            card.setDeckId(newDeckId);
            CardEntity saved = cardRepository.save(card);
            return ResponseEntity.ok(cardMapper.toResponse(saved));
        }
        return ResponseEntity.notFound().build();
    }
}

