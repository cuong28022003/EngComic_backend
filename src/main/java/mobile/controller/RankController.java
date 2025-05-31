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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/rank")
@RequiredArgsConstructor
public class RankController {
    private final RankService rankService;

    @GetMapping("/{xp}")
    public RankResponse getRankByXp(@PathVariable String xp) {
        return rankService.getRankByXp(Integer.parseInt(xp));
    }

    // Get all ranks
//    @GetMapping
//    public List<Rank> getAllRanks() {
//        return rankService.getAllRank();

    @GetMapping
    public List<RankResponse> getAllRankWithCharacterAndPack() {
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
    public ResponseEntity<Rank> createRank(@RequestPart("data") CreateRankRequest createRankRequest,
                                           @RequestPart("image") MultipartFile badge, HttpServletRequest request) {
        String name = createRankRequest.getName();
        int minXp = createRankRequest.getMinXp();
        int maxXp = createRankRequest.getMaxXp();
        int rewardDiamond = createRankRequest.getRewardDiamond();
        String rewardCharacterId = createRankRequest.getRewardCharacterId();

        Rank createdRank = rankService.createRank(name, minXp, maxXp, rewardDiamond, rewardCharacterId, badge);
        return ResponseEntity.ok(createdRank);
    }

    // Update an existing rank
    @PutMapping("/{id}")
    public ResponseEntity<Rank> updateRank(@PathVariable String id, @RequestPart("data") CreateRankRequest updateRankRequest,
                                           @RequestPart(value = "image", required = false) MultipartFile badge, HttpServletRequest request) {
        String name = updateRankRequest.getName();
        int minXp = updateRankRequest.getMinXp();
        int maxXp = updateRankRequest.getMaxXp();
        int rewardDiamond = updateRankRequest.getRewardDiamond();
        String rewardCharacterId = updateRankRequest.getRewardCharacterId();

        Rank updatedRank = rankService.updateRank(new ObjectId(id), name, minXp, maxXp, rewardDiamond, rewardCharacterId, badge);
        return ResponseEntity.ok(updatedRank);
    }

    // Delete a rank
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRank(@PathVariable String id, HttpServletRequest request) {
        rankService.deleteRank(new ObjectId(id));
        return ResponseEntity.noContent().build();
    }
}
