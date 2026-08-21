package mobile.databases.repositories.reader;

import mobile.databases.entities.reader.ToeicUserSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToeicUserSessionRepository extends MongoRepository<ToeicUserSessionEntity, String> {
    List<ToeicUserSessionEntity> findByUserIdAndTestIdOrderBySubmittedAtDesc(String userId, String testId);
    Optional<ToeicUserSessionEntity> findFirstByUserIdAndTestIdOrderBySubmittedAtDesc(String userId, String testId);
    Page<ToeicUserSessionEntity> findByUserIdOrderBySubmittedAtDesc(String userId, Pageable pageable);
}
