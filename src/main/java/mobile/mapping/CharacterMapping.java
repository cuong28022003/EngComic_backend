package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.PackService;
import mobile.model.Entity.Character;
import mobile.model.Entity.CharacterUsage;
import mobile.model.Entity.Pack;
import mobile.model.Entity.UserCharacter;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.character.UserCharacterResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterMapping {

    private final PackService packService;

    public static UserCharacterResponse toUserCharacterResponse(UserCharacter userCharacter, Character character, Pack pack, CharacterUsage characterUsage) {
        UserCharacterResponse userCharacterResponse = new UserCharacterResponse();
        userCharacterResponse.setId(character.getId().toString());
        userCharacterResponse.setName(character.getName());
        userCharacterResponse.setImageUrl(character.getImageUrl());
        userCharacterResponse.setDescription(character.getDescription());
        userCharacterResponse.setRarity(character.getRarity());
        userCharacterResponse.setPack(pack);
        userCharacterResponse.setBonusXp(character.getBonusXp());
        userCharacterResponse.setBonusDiamond(character.getBonusDiamond());
        userCharacterResponse.setVersion(character.getVersion());
        userCharacterResponse.setSkillsUsagePerDay(character.getSkillsUsagePerDay());
        userCharacterResponse.setUsedSkills(characterUsage.getUsedSkills());
        userCharacterResponse.setObtainedAt(userCharacter.getObtainedAt());
        return userCharacterResponse;
    }

    public CharacterResponse toCharacterResponse(Character character) {
        Pack pack = packService.getPackById(character.getPackId());

        CharacterResponse characterResponse = new CharacterResponse();
        characterResponse.setId(character.getId().toHexString());
        characterResponse.setName(character.getName());
        characterResponse.setRarity(character.getRarity());
        characterResponse.setImageUrl(character.getImageUrl());
        characterResponse.setDescription(character.getDescription());
        characterResponse.setPack(pack);
        characterResponse.setBonusXp(character.getBonusXp());
        characterResponse.setBonusDiamond(character.getBonusDiamond());
        characterResponse.setVersion(character.getVersion());
        characterResponse.setSkillsUsagePerDay(character.getSkillsUsagePerDay());
        return characterResponse;
    }
}
