package mobile.repository;

import java.util.Optional;

import mobile.model.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findById(ObjectId id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    User deleteByUsername(String username);
    Optional<User> deleteUserById(ObjectId objectId);
}