package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterStateService;
import mobile.model.Entity.CharacterState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/character-stats")
@RequiredArgsConstructor
public class CharacterStatsController {
    private final CharacterStateService characterStateService;

    @GetMapping("/{id}")
    public ResponseEntity<CharacterState> getById(@PathVariable String id) {
        CharacterState characterState = characterStateService.getById(id);
        return ResponseEntity.ok(characterState);
    }

    @GetMapping("/characters/{characterId}")
    public ResponseEntity<CharacterState> getByCharacterId(@PathVariable String characterId) {
        CharacterState characterState = characterStateService.getByCharacterId(characterId);
        return ResponseEntity.ok(characterState);
    }
}
