package mobile.model.payload.response.character;

import lombok.Data;
import mobile.model.Entity.*;

@Data
public class FullCharacterResponse {
    private String id;
    private CharacterData data;
    private CharacterState states;
    private CharacterAnimation animations;
    private CharacterSprite sprites;
    private CharacterSound sounds;
}
