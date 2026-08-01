package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterDataService;
import mobile.model.Entity.CharacterData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/character-data")
@RequiredArgsConstructor
public class CharacterDataController {
    private final CharacterDataService characterDataService;

    @GetMapping("/{id}")
    public ResponseEntity<CharacterData> getById(@PathVariable String id) {
        CharacterData characterData = characterDataService.getById(id);
        return ResponseEntity.ok(characterData);
    }

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterData> getByCharacterId(@PathVariable String characterId) {
        CharacterData characterData = characterDataService.getByCharacterId(characterId);
        return ResponseEntity.ok(characterData);
    }

    @PostMapping
    public ResponseEntity<CharacterData> createCharacterData(@RequestBody CharacterData characterData) {
        CharacterData created = characterDataService.createCharacterData(characterData);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/characters/{characterId}")
    public ResponseEntity<CharacterData> updateCharacterData(@PathVariable String characterId, @RequestBody CharacterData characterData) {
        CharacterData updated = characterDataService.updateCharacterData(characterId, characterData);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/characters/{characterId}")
    public ResponseEntity<Void> deleteByCharacterId(@PathVariable String characterId) {
        characterDataService.deleteByCharacterId(characterId);
        return ResponseEntity.noContent().build();
    }
}
