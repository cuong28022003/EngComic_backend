package mobile.apis.game;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.game.GetCharacterDataBoundary;
import mobile.businesses.boundaries.game.SaveCharacterDataBoundary;
import mobile.databases.entities.game.CharacterDataEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character-data")
@RequiredArgsConstructor
public class CharacterDataController {

    private final GetCharacterDataBoundary getCharacterDataBoundary;
    private final SaveCharacterDataBoundary saveCharacterDataBoundary;

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterDataEntity> getCharacterDataByCharacterId(@PathVariable String characterId) {
        GetCharacterDataBoundary.Response response = getCharacterDataBoundary.execute(
                GetCharacterDataBoundary.Request.builder().characterId(characterId).build()
        );
        return ResponseEntity.ok(response.getData());
    }

    @PostMapping
    public ResponseEntity<CharacterDataEntity> saveCharacterData(@RequestBody CharacterDataEntity entity) {
        SaveCharacterDataBoundary.Response response = saveCharacterDataBoundary.execute(
                SaveCharacterDataBoundary.Request.builder().entity(entity).build()
        );
        return ResponseEntity.ok(response.getData());
    }
}
