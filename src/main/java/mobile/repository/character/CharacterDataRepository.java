package mobile.repository.character;

import mobile.model.Entity.CharacterData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CharacterDataRepository extends MongoRepository<CharacterData, String> {
    Optional<CharacterData> findByCharacterId(String characterId);
}
