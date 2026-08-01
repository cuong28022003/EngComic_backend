package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterStateService;
import mobile.model.Entity.CharacterState;
import mobile.repository.character.CharacterStatsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CharacterStateServiceImpl implements CharacterStateService {

    private final CharacterStatsRepository characterStatsRepository;

    @Override
    public CharacterState getById(String id) {
        return characterStatsRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Character stats not found"));
    }

    @Override
    public CharacterState getByCharacterId(String characterId) {
        return characterStatsRepository.findByCharacterId(characterId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Character stats not found"));
    }

    @Override
    public CharacterState createCharacterStats(CharacterState characterState) {
        return null;
    }

    @Override
    public CharacterState updateCharacterStats(String id, CharacterState characterState) {
        return null;
    }

    @Override
    public void deleteCharacterStatsById(String id) {

    }
}
