package mobile.apis.card;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.BatchImportRequest;
import mobile.apis.card.dtos.BatchImportResponseDto;
import mobile.apis.card.dtos.CardDetailResponseDto;
import mobile.apis.card.dtos.CardResponseDto;
import mobile.apis.card.dtos.CreateCardRequest;
import mobile.apis.card.dtos.DashboardResponseDto;
import mobile.apis.card.dtos.PracticeResultRequest;
import mobile.businesses.boundaries.card.*;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.security.SecurityUtils;
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

    private final CardRepository cardRepository;
    private final mobile.businesses.interactors.card.CardMapper cardMapper;

    @PostMapping("/batch-import")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BatchImportResponseDto> batchImport(@Valid @RequestBody BatchImportRequest batchRequest) {
        String userId = SecurityUtils.getCurrentUserId();

        BatchImportCard.Request req = BatchImportCard.Request.builder()
                .userId(userId)
                .jsonContent(batchRequest.getJsonContent())
                .deckId(batchRequest.getDeckId())
                .build();

        BatchImportCard.Response response = batchImportCard.execute(req);
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardResponseDto> getDashboard(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String topic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        GetCardDashboard.Request req = GetCardDashboard.Request.builder()
                .userId(userId)
                .search(search)
                .status(status)
                .topic(topic)
                .pageable(pageable)
                .build();

        GetCardDashboard.Response response = getCardDashboard.execute(req);
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CardDetailResponseDto> getCardById(@PathVariable String id) {
        String userId = SecurityUtils.getCurrentUserId();

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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CardResponseDto>> getDueCards(@RequestParam(defaultValue = "15") int limit) {
        String userId = SecurityUtils.getCurrentUserId();

        GetDuePracticeCards.Request req = GetDuePracticeCards.Request.builder()
                .userId(userId)
                .limit(limit)
                .build();

        GetDuePracticeCards.Response response = getDuePracticeCards.execute(req);
        return ResponseEntity.ok(response.getCards());
    }

    @PostMapping("/{id}/practice-result")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CardResponseDto> submitPracticeResult(@PathVariable String id, @Valid @RequestBody PracticeResultRequest resultRequest) {
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

    @GetMapping("/deck/{deckId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CardResponseDto>> getCardsByDeckId(@PathVariable String deckId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardEntity> card = cardRepository.findByDeckId(deckId, pageable);
        Page<CardResponseDto> cardResponsePage = card.map(cardMapper::toResponse);
        return ResponseEntity.ok(cardResponsePage);
    }

    @GetMapping("/deck/{deckId}/due")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CardResponseDto>> getDueCardsByDeckId(@PathVariable String deckId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardEntity> card = cardRepository.findByDeckIdAndNextReviewLessThanEqual(deckId, new java.util.Date(), pageable);
        Page<CardResponseDto> cardResponsePage = card.map(cardMapper::toResponse);
        return ResponseEntity.ok(cardResponsePage);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CardResponseDto>> getCardsByUserId(@PathVariable String userId, @RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        GetUserCardsBoundary.Request boundaryRequest = new GetUserCardsBoundary.Request(userId, search, pageable);
        GetUserCardsBoundary.Response response = getUserCardsBoundary.execute(boundaryRequest);
        return ResponseEntity.ok(response.getCards());
    }

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CardResponseDto> createCard(@RequestBody CreateCardRequest createCardRequest) {
        String currentUserId = SecurityUtils.getCurrentUserId();

        CreateCardBoundary.Request boundaryRequest = new CreateCardBoundary.Request(createCardRequest, currentUserId);
        CreateCardBoundary.Response response = createCardBoundary.execute(boundaryRequest);
        return ResponseEntity.ok(response.getCard());
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CardResponseDto> updateCard(@PathVariable String id, @RequestBody CreateCardRequest createCardRequest) {
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteCard(@PathVariable String id) {
        cardRepository.deleteById(id);
        return ResponseEntity.ok().body("Card deleted successfully");
    }

    @PostMapping("/batch")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CardResponseDto>> createCardsBatch(@RequestBody List<CreateCardRequest> createCardRequests) {
        String currentUserId = SecurityUtils.getCurrentUserId();

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
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
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
