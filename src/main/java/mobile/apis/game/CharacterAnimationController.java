package mobile.apis.game;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.game.GetCharacterAnimationsBoundary;
import mobile.businesses.boundaries.game.SaveCharacterAnimationsBoundary;
import mobile.databases.entities.game.CharacterAnimationEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character-animations")
@RequiredArgsConstructor
public class CharacterAnimationController {

    private final GetCharacterAnimationsBoundary getCharacterAnimations;
    private final SaveCharacterAnimationsBoundary saveCharacterAnimations;

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterAnimationEntity> getAnimationByCharacterId(@PathVariable String characterId) {
        GetCharacterAnimationsBoundary.Response response = getCharacterAnimations.execute(
                GetCharacterAnimationsBoundary.Request.builder().characterId(characterId).build()
        );
        return ResponseEntity.ok(response.getData());
    }

    @PostMapping
    public ResponseEntity<CharacterAnimationEntity> saveAnimationsMetadata(@RequestBody CharacterAnimationEntity entity) {
        SaveCharacterAnimationsBoundary.Response response = saveCharacterAnimations.execute(
                SaveCharacterAnimationsBoundary.Request.builder().entity(entity).build()
        );
        return ResponseEntity.ok(response.getData());
    }
}
