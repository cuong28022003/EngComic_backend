package mobile.repository;

import mobile.model.Entity.Rank;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RankRepository extends MongoRepository<Rank, ObjectId> {
}
