package mobile.Service;

import mobile.model.Entity.CharacterState;

public interface CharacterStateService {
    public CharacterState getById(String id);
    public CharacterState getByCharacterId(String characterId);
    public CharacterState createCharacterStats(CharacterState characterState);
    public CharacterState updateCharacterStats(String id, CharacterState characterState);
    public void deleteCharacterStatsById(String id);
}
