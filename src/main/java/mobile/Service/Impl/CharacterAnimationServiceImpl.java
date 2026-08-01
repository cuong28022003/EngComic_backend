package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterAnimationService;
import mobile.model.Entity.CharacterAnimation;
import mobile.repository.character.CharacterAnimationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CharacterAnimationServiceImpl implements CharacterAnimationService {

    private final CharacterAnimationRepository characterAnimationRepository;

    @Override
    public List<CharacterAnimation> getAllCharacterAnimations() {
        return characterAnimationRepository.findAll();
    }

    @Override
    public CharacterAnimation getCharacterAnimationById(String id) {
        return characterAnimationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CharacterAnimation not found"));
    }

    @Override
    public CharacterAnimation getByCharacterId(String characterId) {
        return characterAnimationRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CharacterAnimation not found"));
    }

    @Override
    public CharacterAnimation saveCharacterAnimation(CharacterAnimation characterAnimation) {
        return characterAnimationRepository.save(characterAnimation);
    }

    @Override
    public void deleteCharacterAnimationById(String id) {
        characterAnimationRepository.deleteById(id);
    }
}
