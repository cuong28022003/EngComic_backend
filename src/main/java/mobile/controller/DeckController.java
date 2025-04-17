package mobile.controller;

import mobile.Service.DeckService;
import mobile.Service.UserService;
import mobile.mapping.DeckMapping;
import mobile.model.Entity.Deck;
import mobile.model.Entity.User;
import mobile.model.payload.request.deck.CreateDeckRequest;
import mobile.model.payload.response.deck.DeckResponse;
import mobile.model.payload.response.deck.DeckStatisticsResponse;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/deck")
public class DeckController {

    @Autowired
    private DeckService deckService;

    @Autowired
    private UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> getDeckById(@PathVariable String id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId deckId = new ObjectId(id);
        Deck deck = deckService.findById(deckId);
        if (deck != null) {
            DeckResponse deckResponse = DeckMapping.entityToResponse(deck);
            DeckStatisticsResponse stats = deckService.getDeckStatistics(deckId);
            deckResponse.setStats(stats); // Assuming DeckResponse has a `setStats` method
            return ResponseEntity.ok(deckResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
        public ResponseEntity<Page<DeckResponse>> getDecksByUserId(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Invalid token");
            }
            String accessToken = authHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken)) {
                throw new RuntimeException("Token expired");
            }
            User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
            if (!userId.equals(user.getId().toHexString())) {
                throw new RuntimeException("Unauthorized access");
            }

            Pageable pageable = PageRequest.of(page, size);
            ObjectId userIdObj = new ObjectId(userId);
            Page<Deck> decks = deckService.findByUserId(userIdObj, pageable);
            if (decks.hasContent()) {
                Page<DeckResponse> deckResponses = decks.map(deck -> {
                    DeckResponse response = DeckMapping.entityToResponse(deck);
                    DeckStatisticsResponse stats = deckService.getDeckStatistics(deck.getId());
                    response.setStats(stats);
                    return response;
                });
                return ResponseEntity.ok(deckResponses);
            } else {
                return ResponseEntity.noContent().build();
            }
        }

    @PostMapping()
    public ResponseEntity<DeckResponse> createDeck(@RequestBody CreateDeckRequest createDeckRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        Deck createdDeck = DeckMapping.createRequestToEntity(createDeckRequest);
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
        createdDeck.setUserId(user.getId());
        Deck deck = deckService.save(createdDeck);
        return ResponseEntity.ok(DeckMapping.entityToResponse(deck));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeckResponse> updateDeck(@PathVariable String id, @RequestBody CreateDeckRequest createDeckRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        ObjectId deckId = new ObjectId(id);
        Deck existingDeck = deckService.findById(deckId);
        if (existingDeck != null) {
            existingDeck.setName(createDeckRequest.getName());
            existingDeck.setDescription(createDeckRequest.getDescription());
            Deck updatedDeck = deckService.save(existingDeck);
            return ResponseEntity.ok(DeckMapping.entityToResponse(updatedDeck));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable String id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        ObjectId deckId = new ObjectId(id);
        deckService.deleteById(deckId);
        return ResponseEntity.noContent().build();
    }
}
