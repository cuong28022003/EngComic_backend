package mobile.databases.repositories.game;

import mobile.databases.entities.game.CharacterAnimationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterAnimationRepository extends MongoRepository<CharacterAnimationEntity, String> {
    Optional<CharacterAnimationEntity> findByCharacterId(String characterId);
}
