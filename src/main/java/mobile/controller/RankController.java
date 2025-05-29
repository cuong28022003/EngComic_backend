package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.RankService;
import mobile.model.Entity.Rank;
import mobile.model.payload.request.rank.CreateRankRequest;
import mobile.model.payload.response.rank.RankResponse;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/rank")
@RequiredArgsConstructor
public class RankController {
    private final RankService rankService;

    @GetMapping("/{xp}")
    public Rank getRankByXp(@PathVariable String xp) {
        return rankService.getRankByXp(Integer.parseInt(xp));
    }

    // Get all ranks
    @GetMapping
    public List<Rank> getAllRanks() {
        return rankService.getAllRank();

  @GetMapping("/fix")
    public List<RankResponse> getAllRank() {
        return rankService.getAllRankWithCharacterAndPack();
    }

    // Get rank by ID
    @GetMapping("/{id}")
    public ResponseEntity<Rank> getRankById(@PathVariable String id) {
        Rank rank = rankService.getRankById(new ObjectId(id));
        return ResponseEntity.ok(rank);
    }

    // Create a new rank
    @PostMapping
    public ResponseEntity<Rank> createRank(@RequestParam String name, @RequestParam int minXp, @RequestParam int maxXp,
            @RequestParam MultipartFile badge) {
        Rank createdRank = rankService.createRank(name, minXp, maxXp, badge);
        return ResponseEntity.ok(createdRank);
    }

    // Update an existing rank
    @PutMapping("/{id}")
    public ResponseEntity<Rank> updateRank(@PathVariable String id, @RequestParam String name, @RequestParam int minXp,
            @RequestParam int maxXp, @RequestParam MultipartFile badge) {
        Rank updatedRank = rankService.updateRank(new ObjectId(id), name, minXp, maxXp, badge);
        return ResponseEntity.ok(updatedRank);
    }

    // Delete a rank
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRank(@PathVariable String id) {
        rankService.deleteRank(new ObjectId(id));
        return ResponseEntity.noContent().build();
    }
}
