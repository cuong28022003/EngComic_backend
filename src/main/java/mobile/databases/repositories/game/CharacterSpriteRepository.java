package mobile.databases.repositories.game;

import mobile.databases.entities.game.CharacterSpriteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterSpriteRepository extends MongoRepository<CharacterSpriteEntity, String> {
    Optional<CharacterSpriteEntity> findByCharacterId(String characterId);
}
