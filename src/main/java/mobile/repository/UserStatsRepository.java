package mobile.repository;

import mobile.model.Entity.UserStats;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@EnableMongoRepositories
public interface UserStatsRepository extends MongoRepository<UserStats, ObjectId> {
    Optional<UserStats> findByUserId(ObjectId userId);
    UserStats save(UserStats userStats);
    Page<UserStats> findAll(Pageable pageable);
}
