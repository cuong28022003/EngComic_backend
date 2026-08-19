package mobile.databases.repositories.comic;

import mobile.databases.entities.comic.SavedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("savedEntityRepository")
public interface SavedRepository extends MongoRepository<SavedEntity, String> {
    Page<SavedEntity> findByUserId(String userId, Pageable pageable);
    Optional<SavedEntity> findByUserIdAndComicId(String userId, String comicId);
    Boolean existsByUserIdAndComicId(String userId, String comicId);
    void deleteByUserIdAndComicId(String userId, String comicId);
}

