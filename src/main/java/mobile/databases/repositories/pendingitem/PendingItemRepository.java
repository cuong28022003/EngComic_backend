package mobile.databases.repositories.pendingitem;

import mobile.databases.entities.pendingitem.PendingItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingItemRepository extends MongoRepository<PendingItemEntity, String> {
    Page<PendingItemEntity> findByUserIdAndStatus(String userId, String status, Pageable pageable);
    Optional<PendingItemEntity> findByUserIdAndContentIgnoreCase(String userId, String content);
    List<PendingItemEntity> findByUserIdAndStatus(String userId, String status);
    List<PendingItemEntity> findByUserId(String userId);
    void deleteByIdAndUserId(String id, String userId);
    long countByUserIdAndStatus(String userId, String status);
}
