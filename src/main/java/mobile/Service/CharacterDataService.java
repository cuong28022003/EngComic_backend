package mobile.Service;

import mobile.model.Entity.CharacterData;

import java.util.Optional;

public interface CharacterDataService {
    public CharacterData getById(String id);
    public CharacterData getByCharacterId(String characterId);
    public CharacterData createCharacterData(CharacterData characterData);
    public CharacterData updateCharacterData(String characterId,CharacterData characterData);
    public void deleteByCharacterId(String characterId);
}
