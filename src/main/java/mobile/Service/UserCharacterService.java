package mobile.Service;

import mobile.model.Entity.UserCharacter;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.character.UserCharacterResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserCharacterService {

    UserCharacter save(ObjectId userId, String characterId);

    List<CharacterResponse> findAllByUserId(ObjectId userId);

    boolean isCharacterOwnedByUser(ObjectId userId, String id);

    Page<CharacterResponse> searchUserCharacters(String name, String rarity, ObjectId userId, Pageable pageable);
}
