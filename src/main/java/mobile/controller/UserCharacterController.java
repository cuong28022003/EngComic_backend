package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.UserCharacterService;
import mobile.model.Entity.UserCharacter;
import mobile.model.payload.request.character.UserCharacterRequest;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user-character")
@RequiredArgsConstructor
public class UserCharacterController {

    private final UserCharacterService userCharacterService;

    @PostMapping
    public ResponseEntity<UserCharacter> createUserCharacter(@RequestBody UserCharacterRequest userCharacter, HttpServletRequest request) {

        ObjectId userId = new ObjectId(userCharacter.getUserId());
        ObjectId characterId = new ObjectId(userCharacter.getCharacterId());
        return ResponseEntity.ok(userCharacterService.save(userId, characterId));
    }

}
