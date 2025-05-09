package mobile.repository;

import mobile.model.Entity.UserCharacterCard;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserCharacterCardRepository extends MongoRepository<UserCharacterCard, ObjectId> {
    List<UserCharacterCard> findByUserId(ObjectId userId);
}