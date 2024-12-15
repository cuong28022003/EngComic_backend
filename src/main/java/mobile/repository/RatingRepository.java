package mobile.repository;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Rating;
import mobile.model.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends MongoRepository<Rating, ObjectId> {
    List<Rating> findByComic(Comic comic);

    List<Rating> findByUser(User user);

    Rating findByComicAndUser(Comic comic, User user);

    void deleteByComicAndUser(Comic comic, User user);
}
