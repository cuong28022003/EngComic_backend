package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterSpriteService;
import mobile.model.Entity.CharacterSprite;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character-sprites")
@RequiredArgsConstructor
public class CharacterSpriteController {

    private final CharacterSpriteService characterSpriteService;

    @GetMapping("/{id}")
    public ResponseEntity<CharacterSprite> getById(@PathVariable String id) {
        CharacterSprite characterSprite = characterSpriteService.findById(id);
        return ResponseEntity.ok(characterSprite);
    }

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterSprite> getByCharacterId(@PathVariable String characterId) {
        CharacterSprite characterSprite = characterSpriteService.getByCharacterId(characterId);
        return ResponseEntity.ok(characterSprite);
    }

    @PostMapping
    public ResponseEntity<CharacterSprite> create(@RequestBody CharacterSprite characterSprite) {
        return ResponseEntity.ok(characterSpriteService.save(characterSprite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        characterSpriteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
