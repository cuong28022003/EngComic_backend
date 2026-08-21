package mobile.databases.repositories.reader;

import mobile.databases.entities.reader.ToeicReviewItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToeicReviewItemRepository extends MongoRepository<ToeicReviewItemEntity, String> {
    List<ToeicReviewItemEntity> findByUserIdAndAttemptIdOrderByQuestionNumberAsc(String userId, String attemptId);
    List<ToeicReviewItemEntity> findByUserIdAndTestIdOrderByQuestionNumberAsc(String userId, String testId);
    Optional<ToeicReviewItemEntity> findByUserIdAndAttemptIdAndQuestionNumber(String userId, String attemptId, int questionNumber);
}
