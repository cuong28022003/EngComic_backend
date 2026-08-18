package mobile.apis.deck;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mobile.Service.UserService;
import mobile.apis.deck.dtos.CreateDeckRequest;
import mobile.apis.deck.dtos.DeckResponseDto;
import mobile.apis.deck.dtos.DeckStatisticsResponse;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.entities.deck.DeckEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.databases.repositories.deck.DeckRepository;
import mobile.domains.deck.DeckRules;
import mobile.model.Entity.User;
import mobile.security.JWT.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;
    private final JwtUtils jwtUtils;

    private User getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        return userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
    }

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
    public ResponseEntity<DeckResponseDto> getDeckById(@PathVariable String id, HttpServletRequest request) {
        getAuthenticatedUser(request);
        DeckEntity deck = deckRepository.findById(id).orElse(null);
        if (deck != null) {
            return ResponseEntity.ok(toResponseDto(deck));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<DeckResponseDto>> getDecksByUserId(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        String currentUserId = (user != null && user.getId() != null) ? user.getId().toHexString() : null;
        if (!userId.equals(currentUserId)) {
            throw new RuntimeException("Unauthorized access");
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
    public ResponseEntity<DeckResponseDto> createDeck(@RequestBody CreateDeckRequest createDeckRequest, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        String userId = (user != null && user.getId() != null) ? user.getId().toHexString() : null;
        DeckEntity createdDeck = new DeckEntity();
        createdDeck.setName(createDeckRequest.getName());
        createdDeck.setDescription(createDeckRequest.getDescription());
        createdDeck.setUserId(userId);
        DeckEntity saved = deckRepository.save(createdDeck);
        return ResponseEntity.ok(toResponseDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeckResponseDto> updateDeck(@PathVariable String id, @RequestBody CreateDeckRequest createDeckRequest, HttpServletRequest request) {
        getAuthenticatedUser(request);
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
    public ResponseEntity<Void> deleteDeck(@PathVariable String id, HttpServletRequest request) {
        getAuthenticatedUser(request);
        cardRepository.deleteAllByDeckId(id);
        deckRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
