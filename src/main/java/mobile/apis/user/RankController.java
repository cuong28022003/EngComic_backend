package mobile.apis.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.RankResponseDto;
import mobile.businesses.boundaries.user.GetRanks;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rank")
@RequiredArgsConstructor
public class RankController {

    private final GetRanks getRanks;

    @GetMapping
    public ResponseEntity<List<RankResponseDto>> getAllRanks() {
        GetRanks.Response response = getRanks.execute();
        return ResponseEntity.ok(response.getRanks());
    }
}
