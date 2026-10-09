package mobile.databases.repositories.game;

import mobile.databases.entities.game.CharacterSoundEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterSoundRepository extends MongoRepository<CharacterSoundEntity, String> {
    Optional<CharacterSoundEntity> findByCharacterId(String characterId);
}
