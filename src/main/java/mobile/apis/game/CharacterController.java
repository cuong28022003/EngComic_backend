package mobile.apis.game;

import lombok.RequiredArgsConstructor;
import mobile.apis.gacha.dtos.UserCharacterResponseDto;
import mobile.businesses.boundaries.game.GetGameCharacterBoundary;
import mobile.businesses.boundaries.gacha.GetUserCharacters;
import mobile.databases.entities.gacha.CharacterEntity;
import mobile.databases.repositories.gacha.CharacterRepository;
import mobile.databases.repositories.gacha.UserCharacterRepository;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final GetGameCharacterBoundary getGameCharacterBoundary;
    private final GetUserCharacters getUserCharacters;
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
    public ResponseEntity<List<UserCharacterResponseDto>> getAllCharactersByUserId(@PathVariable String userId) {
        GetUserCharacters.Request request = GetUserCharacters.Request.builder()
                .userId(userId)
                .pageable(PageRequest.of(0, 1000))
                .build();
        GetUserCharacters.Response response = getUserCharacters.execute(request);
        List<UserCharacterResponseDto> content = response.getUserCharacters().getContent();
        return ResponseEntity.ok(content);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<UserCharacterResponseDto>> getCharactersByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        GetUserCharacters.Request request = GetUserCharacters.Request.builder()
                .userId(userId)
                .pageable(PageRequest.of(page, size))
                .build();
        GetUserCharacters.Response response = getUserCharacters.execute(request);
        return ResponseEntity.ok(response.getUserCharacters());
    }
}
