package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterDataService;
import mobile.model.Entity.CharacterData;
import mobile.repository.character.CharacterDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CharacterDataServiceImpl implements CharacterDataService {

    private final CharacterDataRepository characterDataRepository;

    @Override
    public CharacterData getById(String id) {
        return characterDataRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found")
                );
    }

    @Override
    public CharacterData getByCharacterId(String characterId) {
        return characterDataRepository.findByCharacterId(characterId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found")
        );
    }

    @Override
    public CharacterData createCharacterData(CharacterData characterData) {
        return characterDataRepository.save(characterData);
    }

    @Override
    public CharacterData updateCharacterData(String characterId, CharacterData characterData) {
        CharacterData existingData = getByCharacterId(characterId);
        characterData.setId(existingData.getId());
        characterData.setCharacterId(characterId);
        return characterDataRepository.save(characterData);
    }

    @Override
    public void deleteByCharacterId(String characterId) {
        CharacterData existingData = getByCharacterId(characterId);
        characterDataRepository.delete(existingData);
    }
}
