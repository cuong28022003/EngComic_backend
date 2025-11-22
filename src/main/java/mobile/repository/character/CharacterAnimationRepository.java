package mobile.repository.character;

import mobile.model.Entity.CharacterAnimation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterAnimationRepository extends MongoRepository<CharacterAnimation, String> {
    CharacterAnimation findByCharacterId(String characterId);
}
