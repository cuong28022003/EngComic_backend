package mobile.repository.character;

import mobile.model.Entity.CharacterAnimation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CharacterAnimationRepository extends MongoRepository<CharacterAnimation, String> {
    Optional<CharacterAnimation> findByCharacterId(String characterId);
}
