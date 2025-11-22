package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterAnimationService;
import mobile.model.Entity.CharacterAnimation;
import mobile.repository.character.CharacterAnimationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CharacterAnimationServiceImpl implements CharacterAnimationService {

    private final CharacterAnimationRepository characterAnimationRepository;

    @Override
    public CharacterAnimation getAnimationByCharacterId(String characterId) {
        return characterAnimationRepository.findByCharacterId(characterId);
    }
}
