package mobile.apis.game;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.game.GetGameCharacterBoundary;
import mobile.databases.entities.gacha.CharacterEntity;
import mobile.databases.entities.gacha.UserCharacterEntity;
import mobile.databases.repositories.gacha.CharacterRepository;
import mobile.databases.repositories.gacha.UserCharacterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final GetGameCharacterBoundary getGameCharacterBoundary;
    private final CharacterRepository characterRepository;
    private final UserCharacterRepository userCharacterRepository;

    @GetMapping("/{characterId}")
    public ResponseEntity<CharacterEntity> getCharacterById(@PathVariable String characterId) {
        GetGameCharacterBoundary.Response response = getGameCharacterBoundary.execute(
                GetGameCharacterBoundary.Request.builder().characterId(characterId).build()
        );
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/version/{versionId}")
    public ResponseEntity<List<CharacterEntity>> getCharactersByVersion(@PathVariable String versionId) {
        List<CharacterEntity> characters = characterRepository.findAll();
        return ResponseEntity.ok(characters);
    }

    @GetMapping("/random-enemies")
    public ResponseEntity<List<CharacterEntity>> getRandomEnemies(@RequestParam(defaultValue = "1") int count) {
        List<CharacterEntity> characters = characterRepository.findAll();
        if (characters.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonList(
                    CharacterEntity.builder().id("tsunade_senju_v0").name("Tsunade").rarity("SSR").build()
            ));
        }
        Collections.shuffle(characters);
        return ResponseEntity.ok(characters.stream().limit(count).toList());
    }

    @GetMapping("/users/{userId}/all")
    public ResponseEntity<List<UserCharacterEntity>> getAllCharactersByUserId(@PathVariable String userId) {
        List<UserCharacterEntity> userCharacters = userCharacterRepository.findByUserId(userId);
        return ResponseEntity.ok(userCharacters);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<UserCharacterEntity>> getCharactersByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserCharacterEntity> userCharacters = userCharacterRepository.findByUserId(userId, pageable);
        return ResponseEntity.ok(userCharacters);
    }
}
