package mobile.databases.repositories.game;

import mobile.databases.entities.game.CharacterStatsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterStatsRepository extends MongoRepository<CharacterStatsEntity, String> {
    Optional<CharacterStatsEntity> findByCharacterId(String characterId);
}
