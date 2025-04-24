package mobile.repository;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Rating;
import mobile.model.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends MongoRepository<Rating, ObjectId> {
    Page<Rating> findByComicIdOrderByCreatedAtDesc(ObjectId comicId, Pageable pageable);
    Optional<Rating> findByComicIdAndUserId(ObjectId comicId, ObjectId userId);
    Page<Rating> findByComicId(ObjectId comicId, Pageable pageable);
    List<Rating> findByComicId(ObjectId comicId);
}
