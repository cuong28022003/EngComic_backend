package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterSoundService;
import mobile.model.Entity.CharacterSound;
import mobile.repository.character.CharacterSoundRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CharacterSoundServiceImpl implements CharacterSoundService {
    private final CharacterSoundRepository characterSoundRepository;

    @Override
    public CharacterSound getByCharacterId(String characterId) {
        return characterSoundRepository.findByCharacterId(characterId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found")
        );
    }

    @Override
    public CharacterSound createCharacterSound(CharacterSound characterSound) {
        return characterSoundRepository.save(characterSound);
    }

    @Override
    public CharacterSound updateCharacterSound(String characterId, CharacterSound characterSound) {
        Optional<CharacterSound> existingCharacterSoundOpt = characterSoundRepository.findByCharacterId(characterId);
        if (existingCharacterSoundOpt.isPresent()) {
            CharacterSound existingCharacterSound = existingCharacterSoundOpt.get();
            existingCharacterSound.setSounds(characterSound.getSounds());
            return characterSoundRepository.save(existingCharacterSound);
        } else {
            // Handle the case where the character sound does not exist
            // For simplicity, we can create a new one
            return characterSoundRepository.save(characterSound);
        }
    }

    @Override
    public void deleteByCharacterId(String characterId) {
        Optional<CharacterSound> existingCharacterSoundOpt = characterSoundRepository.findByCharacterId(characterId);
        existingCharacterSoundOpt.ifPresent(characterSound -> characterSoundRepository.delete(characterSound));
    }
}
