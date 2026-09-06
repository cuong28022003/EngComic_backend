package mobile.apis.game;

import lombok.RequiredArgsConstructor;
import mobile.databases.entities.gacha.UserCharacterEntity;
import mobile.databases.repositories.gacha.UserCharacterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user-character")
@RequiredArgsConstructor
public class UserCharacterController {

    private final UserCharacterRepository userCharacterRepository;

    @PostMapping
    public ResponseEntity<UserCharacterEntity> createUserCharacter(@RequestBody UserCharacterEntity entity) {
        if (entity.getObtainedAt() == null) {
            entity.setObtainedAt(LocalDateTime.now());
        }
        UserCharacterEntity saved = userCharacterRepository.save(entity);
        return ResponseEntity.ok(saved);
    }
}
