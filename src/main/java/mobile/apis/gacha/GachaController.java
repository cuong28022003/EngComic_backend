package mobile.apis.gacha;

import lombok.RequiredArgsConstructor;
import mobile.apis.gacha.dtos.CharacterResponseDto;
import mobile.apis.gacha.dtos.PackResponseDto;
import mobile.apis.gacha.dtos.UserCharacterResponseDto;
import mobile.businesses.boundaries.gacha.GetCharacters;
import mobile.businesses.boundaries.gacha.GetPacks;
import mobile.businesses.boundaries.gacha.GetUserCharacters;
import mobile.businesses.boundaries.gacha.RollGacha;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gacha")
@RequiredArgsConstructor
public class GachaController {

    private final GetCharacters getCharacters;
    private final GetPacks getPacks;
    private final GetUserCharacters getUserCharacters;
    private final RollGacha rollGacha;

    @GetMapping("/packs")
    public ResponseEntity<List<PackResponseDto>> getPacks() {
        GetPacks.Response response = getPacks.execute(new GetPacks.Request());
        return ResponseEntity.ok(response.getPacks());
    }

    @GetMapping("/characters")
    public ResponseEntity<Page<CharacterResponseDto>> getCharacters(
            @RequestParam(required = false) String packId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetCharacters.Request request = GetCharacters.Request.builder()
                .packId(packId)
                .pageable(PageRequest.of(page, size))
                .build();

        GetCharacters.Response response = getCharacters.execute(request);
        return ResponseEntity.ok(response.getCharacters());
    }

    @GetMapping("/inventory")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<Page<UserCharacterResponseDto>> getMyInventory(
            @CurrentUserId String currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetUserCharacters.Request request = GetUserCharacters.Request.builder()
                .userId(currentUserId)
                .pageable(PageRequest.of(page, size))
                .build();

        GetUserCharacters.Response response = getUserCharacters.execute(request);
        return ResponseEntity.ok(response.getUserCharacters());
    }

    @PostMapping("/roll")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<?> rollGacha(
            @CurrentUserId String currentUserId,
            @RequestParam(required = false) String packId,
            @RequestParam(required = false) Integer count) {

        int times = (count == null || count < 1) ? 1 : Math.min(count, 10);
        if (times == 1) {
            RollGacha.Request request = RollGacha.Request.builder()
                    .userId(currentUserId)
                    .packId(packId)
                    .build();
            RollGacha.Response response = rollGacha.execute(request);
            return ResponseEntity.ok(response.getCharacter());
        }

        java.util.List<mobile.apis.gacha.dtos.CharacterResponseDto> results = new java.util.ArrayList<>();
        for (int i = 0; i < times; i++) {
            RollGacha.Request request = RollGacha.Request.builder()
                    .userId(currentUserId)
                    .packId(packId)
                    .build();
            RollGacha.Response response = rollGacha.execute(request);
            if (response.getCharacter() != null) {
                results.add(response.getCharacter());
            }
        }
        return ResponseEntity.ok(results);
    }
}

