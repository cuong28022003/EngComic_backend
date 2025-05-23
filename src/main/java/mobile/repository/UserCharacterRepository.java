package mobile.repository;

import mobile.model.Entity.UserCharacter;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface UserCharacterRepository extends MongoRepository<UserCharacter, ObjectId> {
    Page<UserCharacter> findByUserId(ObjectId userId, Pageable pageable);
    List<UserCharacter> findByUserId(ObjectId userId);
}
