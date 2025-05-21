package mobile.controller;

import mobile.Service.CharacterUsageService;
import mobile.model.payload.request.character.CharacterUsageRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/character-usage")
public class CharacterUsageController {
    @Autowired
    private CharacterUsageService usageService;

    @PostMapping("/can-use")
    public boolean canUseSkill(
            @RequestBody CharacterUsageRequest request
    ) {
        ObjectId userId = new ObjectId(request.getUserId());
        ObjectId characterId = new ObjectId(request.getCharacterId());
        LocalDate date = LocalDate.parse(request.getDate());
        String skill = request.getSkill();

        return usageService.canUseSkill(userId, characterId, date, skill);
    }

    @PostMapping("/use")
    public ResponseEntity<String> markSkillUsed(@RequestBody CharacterUsageRequest request) {
        ObjectId userId = new ObjectId(request.getUserId());
        ObjectId characterId = new ObjectId(request.getCharacterId());
        LocalDate date = LocalDate.parse(request.getDate());
        String skill = request.getSkill();

        usageService.markSkillUsed(userId, characterId, date, skill);
        return ResponseEntity.ok("Skill usage marked successfully");
    }

    @PostMapping("/use-skill")
    public ResponseEntity<Map<String, Object>> useSkill(@RequestBody CharacterUsageRequest request) {
        ObjectId userId = new ObjectId(request.getUserId());
        ObjectId characterId = new ObjectId(request.getCharacterId());
        LocalDate date = LocalDate.parse(request.getDate());
        String skill = request.getSkill();

        // Check if the skill can be used
        boolean canUse = usageService.canUseSkill(userId, characterId, date, skill);

        // Prepare the response
        Map<String, Object> response = Map.of(
                "canUse", canUse,
                "message", canUse ? "Skill used successfully." : "Skill cannot be used anymore for today."
        );

        // If the skill can be used, mark it as used
        if (canUse) {
            usageService.markSkillUsed(userId, characterId, date, skill);
        }

        return ResponseEntity.ok(response);
    }
}
