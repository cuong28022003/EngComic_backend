package mobile.repository;

import mobile.model.Entity.Character;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CharacterRepository extends MongoRepository<Character, ObjectId> {
    List<Character> findByRarity(String rarity);
    List<Character> findByPackId(ObjectId packId);
}