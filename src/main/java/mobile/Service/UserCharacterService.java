package mobile.Service;

import mobile.model.Entity.UserCharacter;
import mobile.model.payload.response.character.UserCharacterResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserCharacterService {
    Page<UserCharacterResponse> getCharactersByUserId(ObjectId userId, String searchTerm, String rarity,
                                                      String sortBy, String sortDirection, Pageable pageable);

    UserCharacter save(ObjectId userId, ObjectId characterId);

    List<UserCharacterResponse> findAllByUserId(ObjectId userId);
}
