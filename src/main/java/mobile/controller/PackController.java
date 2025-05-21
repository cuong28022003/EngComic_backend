package mobile.controller;

import mobile.Service.PackService;
import mobile.model.Entity.Pack;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pack")
public class PackController {

    @Autowired
    private PackService packService;

    @PostMapping
    public ResponseEntity<Pack> createPack(@RequestParam String name,
                                           @RequestParam String description,
                                           @RequestParam MultipartFile image) {
        return ResponseEntity.ok(packService.createPack(name, description, image));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pack> getPackById(@PathVariable ObjectId id) {
        return ResponseEntity.ok(packService.getPackById(id));
    }

    @GetMapping
    public ResponseEntity<List<Pack>> getAllPacks() {
        return ResponseEntity.ok(packService.getAllPacks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pack> updatePack(@PathVariable ObjectId id, @RequestParam String name,
                                           @RequestParam String description,
                                           @RequestParam(required = false) MultipartFile image) {
        return ResponseEntity.ok(packService.updatePack(id, name, description, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePack(@PathVariable ObjectId id) {
        packService.deletePack(id);
        return ResponseEntity.noContent().build();
    }
}
