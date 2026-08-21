package mobile.databases.repositories.reader;

import mobile.databases.entities.reader.ToeicMistakeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToeicMistakeRepository extends MongoRepository<ToeicMistakeEntity, String> {
    List<ToeicMistakeEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    Page<ToeicMistakeEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Page<ToeicMistakeEntity> findByUserIdOrderByQuestionNumberAsc(String userId, Pageable pageable);
    List<ToeicMistakeEntity> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
    Page<ToeicMistakeEntity> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status, Pageable pageable);
    Page<ToeicMistakeEntity> findByUserIdAndStatusOrderByQuestionNumberAsc(String userId, String status, Pageable pageable);
    Optional<ToeicMistakeEntity> findByIdAndUserId(String id, String userId);
    List<ToeicMistakeEntity> findByUserIdAndTestIdOrderByQuestionNumberAsc(String userId, String testId);
    long countByUserIdAndStatus(String userId, String status);
    long countByUserId(String userId);
}
