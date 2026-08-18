package mobile.databases.repositories.reading;

import mobile.databases.entities.reading.ReadingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("readingEntityRepository")
public interface ReadingRepository extends MongoRepository<ReadingEntity, String> {
    Page<ReadingEntity> findByUserId(String userId, Pageable pageable);
    Optional<ReadingEntity> findByUserIdAndComicId(String userId, String comicId);
    void deleteByUserIdAndComicId(String userId, String comicId);
}
