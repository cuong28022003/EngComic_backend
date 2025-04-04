package mobile.controller;

import mobile.Service.DeckService;
import mobile.Service.UserService;
import mobile.mapping.DeckMapping;
import mobile.model.Entity.Deck;
import mobile.model.Entity.User;
import mobile.model.payload.request.deck.CreateDeckRequest;
import mobile.security.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/deck")
public class DeckController {

    @Autowired
    private DeckService deckService;

    @Autowired
    private UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/{id}")
    public ResponseEntity<Deck> getDeckById(String id) {
        Deck deck = deckService.findById(id);
        if (deck != null) {
            return ResponseEntity.ok(deck);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Deck>> getDecksByUserId(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring("Bearer ".length());

            if(jwtUtils.validateExpiredToken(accessToken)) {
                throw new RuntimeException("Token expired");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Deck> decks = deckService.findByUserId(userId, pageable);
            if (decks.hasContent()) {
                return ResponseEntity.ok(decks);
            } else {
                return ResponseEntity.noContent().build();
            }

        } else {
            throw new RuntimeException("Invalid token");
        }

    }

    @PostMapping()
        public ResponseEntity<Deck> createDeck(@RequestBody CreateDeckRequest createDeckRequest, HttpServletRequest request) {
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
//            createdDeck.setUserId();
            deckService.save(createdDeck);
            return ResponseEntity.ok(createdDeck);
        }

    @PutMapping("/{id}")
        public ResponseEntity<Deck> updateDeck(@PathVariable String id, @RequestBody CreateDeckRequest createDeckRequest, HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Invalid token");
            }
            String accessToken = authHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken)) {
                throw new RuntimeException("Token expired");
            }

            Deck existingDeck = deckService.findById(id);
            if (existingDeck != null) {
                existingDeck.setName(createDeckRequest.getName());
                existingDeck.setDescription(createDeckRequest.getDescription());
                Deck updatedDeck = deckService.save(existingDeck);
                return ResponseEntity.ok(updatedDeck);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
}
