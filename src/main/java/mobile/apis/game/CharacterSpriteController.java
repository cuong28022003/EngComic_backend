package mobile.apis.game;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.game.GetCharacterSpritesBoundary;
import mobile.businesses.boundaries.game.SaveCharacterSpritesBoundary;
import mobile.databases.entities.game.CharacterSpriteEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character-sprites")
@RequiredArgsConstructor
public class CharacterSpriteController {

    private final GetCharacterSpritesBoundary getCharacterSprites;
    private final SaveCharacterSpritesBoundary saveCharacterSprites;

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterSpriteEntity> getSpritesByCharacterId(@PathVariable String characterId) {
        GetCharacterSpritesBoundary.Response response = getCharacterSprites.execute(
                GetCharacterSpritesBoundary.Request.builder().characterId(characterId).build()
        );
        return ResponseEntity.ok(response.getData());
    }

    @PostMapping
    public ResponseEntity<CharacterSpriteEntity> saveSpritesMetadata(@RequestBody CharacterSpriteEntity entity) {
        SaveCharacterSpritesBoundary.Response response = saveCharacterSprites.execute(
                SaveCharacterSpritesBoundary.Request.builder().entity(entity).build()
        );
        return ResponseEntity.ok(response.getData());
    }
}
