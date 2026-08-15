package mobile.repository.character;

import mobile.model.Entity.Character;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CharacterRepository extends MongoRepository<Character, String> {
    List<Character> findByRarity(String rarity);
    List<Character> findByPackId(String packId);
    List<Character> findByVersion(String version);
    List<Character> findByType(String type);
}
