package mobile.repository.user_character;

import mobile.model.Entity.UserCharacter;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCharacterRepositoryCustom {
    Page<UserCharacter> searchUserCharacters(String name, String rarity, ObjectId userId, Pageable pageable);
}
