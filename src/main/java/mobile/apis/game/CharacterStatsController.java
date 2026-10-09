package mobile.apis.game;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.game.GetCharacterStatsBoundary;
import mobile.businesses.boundaries.game.SaveCharacterStatsBoundary;
import mobile.databases.entities.game.CharacterStatsEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character-stats")
@RequiredArgsConstructor
public class CharacterStatsController {

    private final GetCharacterStatsBoundary getCharacterStatsBoundary;
    private final SaveCharacterStatsBoundary saveCharacterStatsBoundary;

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterStatsEntity> getCharacterStatsByCharacterId(@PathVariable String characterId) {
        GetCharacterStatsBoundary.Response response = getCharacterStatsBoundary.execute(
                GetCharacterStatsBoundary.Request.builder().characterId(characterId).build()
        );
        return ResponseEntity.ok(response.getData());
    }

    @PostMapping
    public ResponseEntity<CharacterStatsEntity> saveCharacterStats(@RequestBody CharacterStatsEntity entity) {
        SaveCharacterStatsBoundary.Response response = saveCharacterStatsBoundary.execute(
                SaveCharacterStatsBoundary.Request.builder().entity(entity).build()
        );
        return ResponseEntity.ok(response.getData());
    }
}
