package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterSpriteService;
import mobile.model.Entity.CharacterSprite;
import mobile.repository.character.CharacterSpriteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CharacterSpriteServiceImpl implements CharacterSpriteService {
    private final CharacterSpriteRepository characterSpriteRepository;

    public CharacterSprite findById(String id) {
        return characterSpriteRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "CharacterSprite not found"));
    }

    public CharacterSprite getByCharacterId(String characterId) {
        return characterSpriteRepository.findByCharacterId(characterId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "CharacterSprite not found"));
    }

    public CharacterSprite save(CharacterSprite characterSprite) {
        return characterSpriteRepository.save(characterSprite);
    }

    public void deleteById(String id) {
        characterSpriteRepository.deleteById(id);
    }
}
