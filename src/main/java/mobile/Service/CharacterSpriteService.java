package mobile.Service;


import mobile.model.Entity.CharacterSprite;

import java.util.Optional;

public interface CharacterSpriteService {
    public CharacterSprite findById(String id);
    public CharacterSprite getByCharacterId(String characterId);
    public CharacterSprite save( CharacterSprite characterSprite);
    public void deleteById(String id);
}
