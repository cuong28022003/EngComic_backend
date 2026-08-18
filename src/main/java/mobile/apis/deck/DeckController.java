package mobile.apis.deck;

import lombok.RequiredArgsConstructor;
import mobile.apis.deck.dtos.CreateDeckRequest;
import mobile.apis.deck.dtos.DeckResponseDto;
import mobile.apis.deck.dtos.DeckStatisticsResponse;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.entities.deck.DeckEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.databases.repositories.deck.DeckRepository;
import mobile.domains.deck.DeckRules;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deck")
@RequiredArgsConstructor
public class DeckController {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    private DeckResponseDto toResponseDto(DeckEntity deck) {
        if (deck == null) return null;
        DeckResponseDto dto = new DeckResponseDto();
        dto.setId(deck.getId());
        dto.setUserId(deck.getUserId());
        dto.setName(deck.getName());
        dto.setDescription(deck.getDescription());
        if (deck.getCreateAt() != null) dto.setCreateAt(deck.getCreateAt().toString());
        if (deck.getUpdateAt() != null) dto.setUpdateAt(deck.getUpdateAt().toString());

        DeckStatisticsResponse stats = getDeckStatistics(deck.getId());
        dto.setStats(stats);
        return dto;
    }

    private DeckStatisticsResponse getDeckStatistics(String deckId) {
        List<CardEntity> cards = cardRepository.findByDeckId(deckId);
        List<DeckRules.CardProgress> cardProgressList = cards.stream()
                .map(c -> new DeckRules.CardProgress(
                        c.getId(),
                        c.getRepetition(),
                        c.getInterval(),
                        c.getEaseFactor(),
                        c.getWrongCount(),
                        c.getReviewCount(),
                        c.getNextReview(),
                        c.isFavorite()
                ))
                .collect(Collectors.toList());

        DeckRules.DeckStatistics summary = DeckRules.calculateStatistics(cardProgressList, new Date());
        return new DeckStatisticsResponse(
                summary.totalCards(),
                summary.totalNew(),
                summary.totalEasy(),
                summary.totalHard(),
                summary.totalDue()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_DECK_MANAGE)
    public ResponseEntity<DeckResponseDto> getDeckById(@PathVariable String id) {
        DeckEntity deck = deckRepository.findById(id).orElse(null);
        if (deck != null) {
            return ResponseEntity.ok(toResponseDto(deck));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize(AppAuthorities.HAS_DECK_MANAGE)
    public ResponseEntity<Page<DeckResponseDto>> getDecksByUserId(
            @CurrentUserId String currentUserId,
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (currentUserId != null && !userId.equals(currentUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized access to user decks");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<DeckEntity> decks = deckRepository.findByUserId(userId, pageable);
        if (decks.hasContent()) {
            Page<DeckResponseDto> deckResponses = decks.map(this::toResponseDto);
            return ResponseEntity.ok(deckResponses);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping()
    @PreAuthorize(AppAuthorities.HAS_DECK_MANAGE)
    public ResponseEntity<DeckResponseDto> createDeck(
            @CurrentUserId String userId,
            @RequestBody CreateDeckRequest createDeckRequest) {
        DeckEntity createdDeck = new DeckEntity();
        createdDeck.setName(createDeckRequest.getName());
        createdDeck.setDescription(createDeckRequest.getDescription());
        createdDeck.setUserId(userId);
        DeckEntity saved = deckRepository.save(createdDeck);
        return ResponseEntity.ok(toResponseDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_DECK_MANAGE)
    public ResponseEntity<DeckResponseDto> updateDeck(
            @PathVariable String id,
            @RequestBody CreateDeckRequest createDeckRequest) {
        DeckEntity existingDeck = deckRepository.findById(id).orElse(null);
        if (existingDeck != null) {
            existingDeck.setName(createDeckRequest.getName());
            existingDeck.setDescription(createDeckRequest.getDescription());
            DeckEntity updatedDeck = deckRepository.save(existingDeck);
            return ResponseEntity.ok(toResponseDto(updatedDeck));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_DECK_MANAGE)
    public ResponseEntity<Void> deleteDeck(@PathVariable String id) {
        cardRepository.deleteAllByDeckId(id);
        deckRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
