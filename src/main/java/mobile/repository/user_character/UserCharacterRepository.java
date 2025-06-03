package mobile.repository.user_character;

import mobile.model.Entity.UserCharacter;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface UserCharacterRepository extends MongoRepository<UserCharacter, ObjectId>, UserCharacterRepositoryCustom {
    Page<UserCharacter> findByUserId(ObjectId userId, Pageable pageable);
    List<UserCharacter> findByUserId(ObjectId userId);

    boolean existsByUserIdAndCharacterId(ObjectId userId, ObjectId id);
}
