package mobile.Service;

import mobile.model.Entity.CharacterAnimation;

public interface CharacterAnimationService {
    CharacterAnimation getAnimationByCharacterId(String characterId);
}
