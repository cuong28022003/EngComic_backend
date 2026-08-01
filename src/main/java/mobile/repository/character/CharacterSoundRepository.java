package mobile.repository.character;

import mobile.model.Entity.CharacterSound;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterSoundRepository extends MongoRepository<CharacterSound, String> {
    Optional<CharacterSound> findByCharacterId(String characterId);
}
