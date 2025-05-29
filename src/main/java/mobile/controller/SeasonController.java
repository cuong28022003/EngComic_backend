package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.SeasonService;
import mobile.model.Entity.Season;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/season")
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonService seasonService;

    @PostMapping
    public ResponseEntity<Season> createSeason(@RequestBody Season season) {
        return ResponseEntity.ok(seasonService.createSeason(season));
    }

    @GetMapping
    public ResponseEntity<List<Season>> getAllSeasons() {
        return ResponseEntity.ok(seasonService.getAllSeasons());
    }

    @GetMapping("/current")
    public ResponseEntity<Season> getCurrentSeason() {
        return ResponseEntity.ok(seasonService.getCurrentSeason());
    }

    @PostMapping("/end")
    public ResponseEntity<Season> endSeasonAndReward() {
        return ResponseEntity.ok(seasonService.endCurrentSeasonAndDistributeRewards());
    }
}
