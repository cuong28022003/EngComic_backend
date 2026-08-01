package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterSoundService;
import mobile.model.Entity.CharacterSound;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character-sounds")
@RequiredArgsConstructor
public class CharacterSoundController {
    private final CharacterSoundService characterSoundService;

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterSound> getCharacterSoundByCharacterId(@PathVariable String characterId) {
        CharacterSound characterSound = characterSoundService.getByCharacterId(characterId);
        return ResponseEntity.ok(characterSound);
    }

    @PostMapping
    public ResponseEntity<CharacterSound> createCharacterSound(@RequestBody CharacterSound characterSound) {
        CharacterSound createdCharacterSound = characterSoundService.createCharacterSound(characterSound);
        return ResponseEntity.ok(createdCharacterSound);
    }

}
