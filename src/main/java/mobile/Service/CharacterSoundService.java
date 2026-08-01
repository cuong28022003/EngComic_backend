package mobile.Service;

import mobile.model.Entity.CharacterSound;

import java.util.Optional;

public interface CharacterSoundService {
    public CharacterSound getByCharacterId(String characterId);
    public CharacterSound createCharacterSound(CharacterSound characterSound);
    public CharacterSound updateCharacterSound(String characterId,CharacterSound characterSound);
    public void deleteByCharacterId(String characterId);
}
