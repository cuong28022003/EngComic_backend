package mobile.databases.repositories.vocab;

import mobile.databases.entities.vocab.PendingItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("vocabPendingItemRepository")
public interface PendingItemRepository extends MongoRepository<PendingItemEntity, String> {
    Page<PendingItemEntity> findByUserIdAndStatus(String userId, String status, Pageable pageable);
    List<PendingItemEntity> findByUserIdAndStatus(String userId, String status);
    Optional<PendingItemEntity> findByIdAndUserId(String id, String userId);
    Optional<PendingItemEntity> findByUserIdAndContentIgnoreCase(String userId, String content);
    boolean existsByUserIdAndContentIgnoreCase(String userId, String content);
    void deleteByIdAndUserId(String id, String userId);
}
