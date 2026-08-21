package mobile.databases.repositories.reader;

import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToeicTestAttemptRepository extends MongoRepository<ToeicTestAttemptEntity, String> {
    List<ToeicTestAttemptEntity> findByUserIdAndTestIdOrderByStartedAtDesc(String userId, String testId);
    Optional<ToeicTestAttemptEntity> findFirstByUserIdAndTestIdAndStatusOrderByStartedAtDesc(String userId, String testId, String status);
    Optional<ToeicTestAttemptEntity> findFirstByUserIdAndStatusOrderByLastSavedAtDesc(String userId, String status);
    Optional<ToeicTestAttemptEntity> findByIdAndUserId(String id, String userId);
    long countByUserIdAndTestId(String userId, String testId);
}
