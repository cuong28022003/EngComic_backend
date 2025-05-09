package mobile.controller;

import mobile.Service.CharacterService;
import mobile.model.Entity.Character;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/character-card")
public class CharacterController {

    @Autowired
    private CharacterService characterService;

    @GetMapping
    public ResponseEntity<List<Character>> getAllCharacterCards() {
        List<Character> characters = characterService.findAll();
        return ResponseEntity.ok(characters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacterCardById(@PathVariable ObjectId id) {
        Character character = characterService.findById(id);
        return ResponseEntity.ok(character);
    }

    @PostMapping
    public ResponseEntity<Character> createCharacterCard(@RequestParam String name,
                                                         @RequestParam String description,
                                                         @RequestParam MultipartFile image,
                                                         @RequestParam String rarity) {
        Character createdCard = characterService.create(name, description, rarity, image);
        return ResponseEntity.ok(createdCard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacterCard(@PathVariable ObjectId id,
                                                         @RequestParam String name,
                                                         @RequestParam String description,
                                                         @RequestParam MultipartFile image,
                                                         @RequestParam String rarity) {
        Character updatedCard = characterService.update(id, name, description, rarity, image);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacterCard(@PathVariable ObjectId id) {
        characterService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}