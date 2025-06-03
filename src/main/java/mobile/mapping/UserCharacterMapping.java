package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterUsageService;
import mobile.model.Entity.CharacterUsage;
import mobile.model.Entity.Pack;
import mobile.model.Entity.UserCharacter;
import mobile.model.payload.response.character.UserCharacterResponse;
import mobile.repository.CharacterRepository;
import mobile.repository.CharacterUsageRepository;
import mobile.repository.PackRepository;
import org.springframework.stereotype.Component;
import mobile.model.Entity.Character;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserCharacterMapping {
    private final CharacterRepository characterRepository;
    private final CharacterUsageService characterUsageService;
    private final PackRepository packRepository;
    private final CharacterMapping characterMapping;

    public UserCharacterResponse mapToUserCharacterResponse(UserCharacter userCharacter) {
        Character character = characterRepository.findById(userCharacter.getCharacterId())
                .orElseThrow(() -> new RuntimeException("Character not found"));
        Pack pack = packRepository.findById(character.getPackId())
                .orElseThrow(() -> new RuntimeException("Pack not found"));
        CharacterUsage characterUsage = characterUsageService.getOrCreateUsage(
                userCharacter.getUserId(),
                character.getId(),
                LocalDate.now()
        );
        UserCharacterResponse response = new UserCharacterResponse();
        response.setId(character.getId().toString());
        response.setName(character.getName());
        response.setImageUrl(character.getImageUrl());
        response.setDescription(character.getDescription());
        response.setRarity(character.getRarity());
        response.setPack(pack);
        response.setBonusXp(character.getBonusXp());
        response.setBonusDiamond(character.getBonusDiamond());
        response.setVersion(character.getVersion());
        response.setSkillsUsagePerDay(character.getSkillsUsagePerDay());
        response.setUsedSkills(characterUsage.getUsedSkills());
        response.setObtainedAt(userCharacter.getObtainedAt());
        return response;



    }
}

//private String id;
//private String name;
//private String imageUrl;
//private String description;
//private String rarity;
//private Pack pack;
//private int bonusXp;
//private int bonusDiamond;
//private String version; // phiên bản của thẻ, dùng để quản lý các thay đổi trong tương lai
//
//private Map<String, Integer> skillsUsagePerDay; // {"DOUBLE_XP": 1, "SHOW_ANSWER": 2}
//private Map<String, Integer> usedSkills; // {"DOUBLE_XP": 1, "SHOW_ANSWER": 2} - số lần sử dụng kỹ năng trong ngày
//
//private LocalDateTime obtainedAt; // ngày nhận thẻ