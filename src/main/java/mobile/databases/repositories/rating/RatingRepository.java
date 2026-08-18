package mobile.databases.repositories.rating;

import mobile.databases.entities.rating.RatingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("ratingEntityRepository")
public interface RatingRepository extends MongoRepository<RatingEntity, String> {
    Page<RatingEntity> findByComicId(String comicId, Pageable pageable);
    List<RatingEntity> findByComicId(String comicId);
    Optional<RatingEntity> findByUserIdAndComicId(String userId, String comicId);
}
