package mobile.repository;

import mobile.model.Entity.Pack;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PackRepository extends MongoRepository<Pack, ObjectId> {
    List<Pack> findAll();
}
