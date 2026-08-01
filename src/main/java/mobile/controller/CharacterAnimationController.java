package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterAnimationService;
import mobile.model.Entity.CharacterAnimation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/character-animations")
@RequiredArgsConstructor
public class CharacterAnimationController {
    private final CharacterAnimationService characterAnimationService;

    @GetMapping
    public ResponseEntity<List<CharacterAnimation>> getAll() {
        return ResponseEntity.ok(characterAnimationService.getAllCharacterAnimations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterAnimation> getById(@PathVariable String id) {
        CharacterAnimation characterAnimation = characterAnimationService.getCharacterAnimationById(id);
        return ResponseEntity.ok(characterAnimation);
    }

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterAnimation> getByCharacterId(@PathVariable String characterId) {
        CharacterAnimation characterAnimation = characterAnimationService.getByCharacterId(characterId);
        return ResponseEntity.ok(characterAnimation);
    }

    @PostMapping
    public ResponseEntity<CharacterAnimation> create(@RequestBody CharacterAnimation characterAnimation) {
        return ResponseEntity.ok(characterAnimationService.saveCharacterAnimation(characterAnimation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterAnimation> update(@PathVariable String id, @RequestBody CharacterAnimation characterAnimation) {
        characterAnimation.setId(id);
        return ResponseEntity.ok(characterAnimationService.saveCharacterAnimation(characterAnimation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        characterAnimationService.deleteCharacterAnimationById(id);
        return ResponseEntity.noContent().build();
    }
}
