package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterAnimationService;
import mobile.Service.PackService;
import mobile.model.Entity.*;
import mobile.model.Entity.Character;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.character.UserCharacterResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterMapping {

    private final PackService packService;
    private final CharacterAnimationService characterAnimationService;

    public CharacterResponse toCharacterResponse(Character character) {
        Pack pack = packService.getPackById(character.getPackId());
        CharacterAnimation animation = characterAnimationService.getAnimationByCharacterId(character.getId().toHexString());

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

        if (animation != null) {
            characterResponse.setSpriteSheetUrl(animation.getSpriteSheetUrl());
            characterResponse.setFrameWidth(animation.getFrameWidth());
            characterResponse.setFrameHeight(animation.getFrameHeight());
            characterResponse.setFps(animation.getFps());
            characterResponse.setAnimations(animation.getAnimations());
        }
        characterResponse.setScale(character.getScale());
        characterResponse.setPosition(character.getPosition());
        characterResponse.setWidth(character.getWidth());
        characterResponse.setHeight(character.getHeight());
        characterResponse.setOffset(character.getOffset());
        characterResponse.setAttackBox(character.getAttackBox());
        characterResponse.setStats(character.getStats());
        characterResponse.setSprites(character.getSprites());
        characterResponse.setTransformationCharacterId(character.getTransformationCharacterId());
        return characterResponse;
    }
}
