package mobile.apis.game;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.game.GetCharacterSoundsBoundary;
import mobile.businesses.boundaries.game.SaveCharacterSoundsBoundary;
import mobile.databases.entities.game.CharacterSoundEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character-sounds")
@RequiredArgsConstructor
public class CharacterSoundController {

    private final GetCharacterSoundsBoundary getCharacterSoundsBoundary;
    private final SaveCharacterSoundsBoundary saveCharacterSoundsBoundary;

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterSoundEntity> getCharacterSoundsByCharacterId(@PathVariable String characterId) {
        GetCharacterSoundsBoundary.Response response = getCharacterSoundsBoundary.execute(
                GetCharacterSoundsBoundary.Request.builder().characterId(characterId).build()
        );
        return ResponseEntity.ok(response.getData());
    }

    @PostMapping
    public ResponseEntity<CharacterSoundEntity> saveSoundsMetadata(@RequestBody CharacterSoundEntity entity) {
        SaveCharacterSoundsBoundary.Response response = saveCharacterSoundsBoundary.execute(
                SaveCharacterSoundsBoundary.Request.builder().entity(entity).build()
        );
        return ResponseEntity.ok(response.getData());
    }
}
