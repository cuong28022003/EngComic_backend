package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterService;
import mobile.Service.PackService;
import mobile.Service.UserCharacterService;
import mobile.mapping.CharacterMapping;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;
import mobile.model.payload.request.character.CharacterRequest;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.character.FullCharacterResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterMapping characterMapping;
    private final CharacterService characterService;
    private final UserCharacterService userCharacterService;
    private final PackService packService;

    @GetMapping
    public ResponseEntity<List<Character>> getAllCharacterCards() {
        List<Character> characters = characterService.findAll();
        return ResponseEntity.ok(characters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getCharacterById(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("The given id must not be null or empty!");
        }
        CharacterResponse character = characterService.findById(id);
        return ResponseEntity.ok(character);
    }

    @GetMapping("/users/{userId}/all")
    public ResponseEntity<List<CharacterResponse>> getAllUserCharacters(@PathVariable String userId, HttpServletRequest request) {
        List<CharacterResponse> userCharacters = userCharacterService.findAllByUserId(new ObjectId(userId));
        return ResponseEntity.ok(userCharacters);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<CharacterResponse>> searchFilterAndSortCharacters(
            @PathVariable String userId,
            @RequestParam(required = false) String searchTerm, // Search by name or pack
            @RequestParam(required = false) String rarity,   // Filter by rarity
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) { // Pagination parameters
        Page<CharacterResponse> userCharacters = userCharacterService.searchUserCharacters(
                searchTerm,
                rarity,
                new ObjectId(userId),
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(userCharacters);
    }

    @PostMapping
    public ResponseEntity<Character> createCharacterCard(@RequestPart("data") CharacterRequest characterData,
                                                         @RequestPart("image") MultipartFile image) {
        Character createdCard = characterService.create(
                characterData.getName(),
                characterData.getDescription(),
                characterData.getRarity(),
                image,
                characterData.getPackId(),
                characterData.getBonusXp(),
                characterData.getBonusDiamond(),
                characterData.getVersion(),
                characterData.getSkillsUsagePerDay()
        );
        return ResponseEntity.ok(createdCard);
    }

@PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacterCard(@PathVariable String id,
                                                         @RequestPart("data") CharacterRequest characterData,
                                                         @RequestPart("image") MultipartFile image) {
        Character updatedCard = characterService.update(
                id,
                characterData.getName(),
                characterData.getDescription(),
                characterData.getRarity(),
                image,
                characterData.getBonusXp(),
                characterData.getBonusDiamond(),
                characterData.getVersion(),
                characterData.getSkillsUsagePerDay()
        );
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacterCard(@PathVariable String id) {
        characterService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/version/{version}")
    public ResponseEntity<List<CharacterResponse>> getCharactersByVersion(@PathVariable String version) {
        List<Character> characters = characterService.findByVersion(version);
        List<CharacterResponse> characterResponses = characters.stream()
                .map(character -> {
                    Pack pack = packService.getPackById(character.getPackId());
                    return characterMapping.toCharacterResponse(character);
                }).collect(Collectors.toList());
        return ResponseEntity.ok(characterResponses);
    }

    @GetMapping("/random-enemies")
    public ResponseEntity<List<CharacterResponse>> getRandomEnemies(@RequestParam int count) {
        List<CharacterResponse> randomEnemies = characterService.findRandomEnemies(count);
        return ResponseEntity.ok(randomEnemies);
    }
}
