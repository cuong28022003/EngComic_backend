package mobile.repository.character;

import mobile.model.Entity.CharacterSprite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CharacterSpriteRepository extends MongoRepository<CharacterSprite, String> {
    Optional<CharacterSprite> findByCharacterId(String characterId);
}
