package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.PackService;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;
import mobile.model.Entity.UserCharacter;
import mobile.model.payload.response.character.UserCharacterResponse;

public class CharacterMapping {

    public static UserCharacterResponse toUserCharacterResponse(UserCharacter userCharacter, Character character, Pack pack) {
        UserCharacterResponse userCharacterResponse = new UserCharacterResponse();
        userCharacterResponse.setCharacter(character);
        userCharacterResponse.setPack(pack);
        userCharacterResponse.setObtainedAt(userCharacter.getObtainedAt());
        return userCharacterResponse;
    }
}
