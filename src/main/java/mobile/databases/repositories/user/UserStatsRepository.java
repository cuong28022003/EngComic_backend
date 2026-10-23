package mobile.databases.repositories.user;

import mobile.databases.entities.user.UserStatsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("userStatsEntityRepository")
public interface UserStatsRepository extends MongoRepository<UserStatsEntity, String> {
    Optional<UserStatsEntity> findByUserId(String userId);
    Page<UserStatsEntity> findByXpGreaterThan(int xp, Pageable pageable);
}

