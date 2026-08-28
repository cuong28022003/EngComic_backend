package mobile.databases.repositories.user;

import mobile.databases.entities.user.UserLearningStatsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLearningStatsRepository extends MongoRepository<UserLearningStatsEntity, String> {
    Optional<UserLearningStatsEntity> findByUserId(String userId);
}
