package mobile.databases.repositories.reader;

import mobile.databases.entities.reader.ToeicTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToeicTestRepository extends MongoRepository<ToeicTestEntity, String> {
    List<ToeicTestEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    Page<ToeicTestEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Optional<ToeicTestEntity> findByIdAndUserId(String id, String userId);
    long countByUserId(String userId);
    long countByUserIdAndStatus(String userId, String status);
}
