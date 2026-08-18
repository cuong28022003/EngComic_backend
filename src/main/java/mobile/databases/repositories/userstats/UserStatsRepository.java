package mobile.databases.repositories.userstats;

import mobile.databases.entities.userstats.UserStatsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("userStatsEntityRepository")
public interface UserStatsRepository extends MongoRepository<UserStatsEntity, String> {
    Optional<UserStatsEntity> findByUserId(String userId);
}
