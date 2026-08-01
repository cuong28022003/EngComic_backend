package mobile.repository.character;

import mobile.model.Entity.CharacterState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterStatsRepository extends MongoRepository<CharacterState, String> {
    Optional<CharacterState> findByCharacterId(String characterId);
}
