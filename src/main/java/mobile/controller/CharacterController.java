package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterService;
import mobile.Service.UserCharacterService;
import mobile.model.Entity.Character;
import mobile.model.payload.response.character.UserCharacterResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/character")
public class CharacterController {

    private final CharacterService characterService;
    private final UserCharacterService userCharacterService;

    @GetMapping
    public ResponseEntity<List<Character>> getAllCharacterCards() {
        List<Character> characters = characterService.findAll();
        return ResponseEntity.ok(characters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacterCardById(@PathVariable ObjectId id) {
        Character character = characterService.findById(id);
        return ResponseEntity.ok(character);
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<UserCharacterResponse>> getAllUserCharacters(@PathVariable String userId, HttpServletRequest request) {
        List<UserCharacterResponse> userCharacters = userCharacterService.findAllByUserId(new ObjectId(userId));
        return ResponseEntity.ok(userCharacters);
    }

    @GetMapping("/user/{userId}")
    public Page<UserCharacterResponse> searchFilterAndSortCharacters(
            @PathVariable String userId,
            @RequestParam(required = false) String searchTerm, // Search by name or packName
            @RequestParam(required = false) String rarity,   // Filter by rarity
            @RequestParam(defaultValue = "name") String sortBy, // Sort by name, rarity, or obtainedAt
            @RequestParam(defaultValue = "asc") String sortDirection, // Sort direction: asc or desc
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) { // Pagination parameters
        return userCharacterService.getCharactersByUserId(
                new ObjectId(userId), searchTerm, rarity, sortBy, sortDirection, PageRequest.of(page, size));
    }

    @PostMapping
    public ResponseEntity<Character> createCharacterCard(@RequestParam String name,
                                                         @RequestParam String description,
                                                         @RequestParam MultipartFile image,
                                                         @RequestParam String rarity,
                                                         @RequestParam ObjectId packId) {
        Character createdCard = characterService.create(name, description, rarity, image, packId);
        return ResponseEntity.ok(createdCard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacterCard(@PathVariable ObjectId id,
                                                         @RequestParam String name,
                                                         @RequestParam String description,
                                                         @RequestParam MultipartFile image,
                                                         @RequestParam String rarity) {
        Character updatedCard = characterService.update(id, name, description, rarity, image);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacterCard(@PathVariable ObjectId id) {
        characterService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}