package mobile.repository;

import mobile.model.Entity.CharacterUsage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CharacterUsageRepository extends MongoRepository<CharacterUsage, ObjectId> {
    Optional<CharacterUsage> findByUserIdAndCharacterIdAndDate(ObjectId userId, ObjectId characterId, LocalDate date);
}
