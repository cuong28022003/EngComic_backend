package mobile.Service;

import mobile.model.Entity.CharacterAnimation;

import java.util.List;
import java.util.Optional;

public interface CharacterAnimationService {
    List<CharacterAnimation> getAllCharacterAnimations();
    CharacterAnimation getCharacterAnimationById(String id);
    CharacterAnimation getByCharacterId(String characterId);
    CharacterAnimation saveCharacterAnimation(CharacterAnimation characterAnimation);
    void deleteCharacterAnimationById(String id);
}
