package mobile.controller;

import mobile.Service.CardService;
import mobile.Service.DeckService;
import mobile.Service.UserService;
import mobile.mapping.CardMapping;
import mobile.model.Entity.Card;
import mobile.model.Entity.Deck;
import mobile.model.Entity.User;
import mobile.model.payload.request.card.CreateCardRequest;
import mobile.model.payload.request.card.CardReviewRequest;
import mobile.model.payload.response.card.CardResponse;
import mobile.model.payload.response.card.CardReviewResponse;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import mobile.businesses.boundaries.card.GetUserCardsBoundary;
import mobile.businesses.boundaries.card.CreateCardBoundary;
import mobile.repository.CardRepository;

@RestController
@RequestMapping("/api/card")
public class CardController {

    @Autowired
    private GetUserCardsBoundary getUserCardsBoundary;

    @Autowired
    private CreateCardBoundary createCardBoundary;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private DeckService deckService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable String id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId cardId = new ObjectId(id);
        Card card = cardService.findById(cardId);
        if (card != null) {
            CardResponse cardResponse = CardMapping.entityToResponse(card);
            return ResponseEntity.ok(cardResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/deck/{deckId}")
    public ResponseEntity<Page<CardResponse>> getCardsByDeckId(@PathVariable String deckId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId deckObjectId = new ObjectId(deckId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> card = cardService.findByDeckId(deckObjectId, pageable);
        Page<CardResponse> cardResponsePage = card.map(CardMapping::entityToResponse);
        return ResponseEntity.ok(cardResponsePage);
    }

    @GetMapping("/deck/{deckId}/due")
    public ResponseEntity<Page<CardResponse>> getDueCardsByDeckId(@PathVariable String deckId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId deckObjectId = new ObjectId(deckId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> card = cardService.findDueCardsByDeckId(deckObjectId, pageable);
        Page<CardResponse> cardResponsePage = card.map(CardMapping::entityToResponse);
        return ResponseEntity.ok(cardResponsePage);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CardResponse>> getCardsByUserId(@PathVariable String userId, @RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        Pageable pageable = PageRequest.of(page, size);
        GetUserCardsBoundary.Request boundaryRequest = new GetUserCardsBoundary.Request(userId, search, pageable);

        Page<CardResponse> response = getUserCardsBoundary.execute(boundaryRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<CardResponse> createCard(@RequestBody CreateCardRequest createCardRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        User currentUser = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
        String currentUserId = (currentUser != null && currentUser.getId() != null) ? currentUser.getId().toHexString() : null;

        CreateCardBoundary.Request boundaryRequest = new CreateCardBoundary.Request(createCardRequest, currentUserId);

        CardResponse response = createCardBoundary.execute(boundaryRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable String id, @RequestBody CreateCardRequest createCardRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId cardId = new ObjectId(id);
        Card existingCard = cardService.findById(cardId);
        if (existingCard != null) {
            Card updatedCard = CardMapping.createRequestToEntity(createCardRequest);
            updatedCard.setId(cardId);
            updatedCard.setCreateAt(existingCard.getCreateAt());
            Card savedCard = cardService.save(updatedCard);
            CardResponse cardResponse = CardMapping.entityToResponse(savedCard);
            return ResponseEntity.ok(cardResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable String id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId cardId = new ObjectId(id);
        cardService.deleteById(cardId);
        return ResponseEntity.ok().body("Card deleted successfully");
    }

    @PostMapping("/review")
    public ResponseEntity<CardReviewResponse> reviewCard(@RequestBody CardReviewRequest cardReviewRequest,
                                                         HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

        ObjectId userIdObj = user.getId();
        ObjectId cardIdObj = new ObjectId(cardReviewRequest.getCardId());
        boolean isCorrect = cardReviewRequest.isCorrect();
        String reviewState = cardReviewRequest.getReviewState();
        Card review = cardService.review(cardIdObj, isCorrect, reviewState);
        if (review != null) {
            CardReviewResponse cardReviewResponse = CardMapping.entityToCardReviewResponse(review);
            return ResponseEntity.ok(cardReviewResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<CardResponse>> createCardsBatch(@RequestBody List<CreateCardRequest> createCardRequests, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        User currentUser = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
        List<CardResponse> responses = createCardRequests.stream().map(req -> {
            Card cardEntity = CardMapping.createRequestToEntity(req);
            if (cardEntity.getUserId() == null && currentUser != null) {
                cardEntity.setUserId(currentUser.getId());
            }
            Card saved = cardService.save(cardEntity);
            return CardMapping.entityToResponse(saved);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/toggle-favorite")
    public ResponseEntity<CardResponse> toggleFavorite(@PathVariable String id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId cardId = new ObjectId(id);
        Card card = cardService.findById(cardId);
        if (card != null) {
            card.setFavorite(!card.isFavorite());
            Card saved = cardService.save(card);
            return ResponseEntity.ok(CardMapping.entityToResponse(saved));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/move-deck")
    public ResponseEntity<CardResponse> moveDeck(@PathVariable String id, @RequestParam String newDeckId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId cardId = new ObjectId(id);
        Card card = cardService.findById(cardId);
        if (card != null) {
            card.setDeckId(new ObjectId(newDeckId));
            Card saved = cardService.save(card);
            return ResponseEntity.ok(CardMapping.entityToResponse(saved));
        }
        return ResponseEntity.notFound().build();
    }
}
