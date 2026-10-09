package mobile.databases.repositories.game;

import mobile.databases.entities.game.CharacterDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterDataRepository extends MongoRepository<CharacterDataEntity, String> {
    Optional<CharacterDataEntity> findByCharacterId(String characterId);
}
