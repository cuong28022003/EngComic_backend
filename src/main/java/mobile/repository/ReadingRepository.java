package mobile.repository;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Reading;

import mobile.model.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import org.springframework.data.domain.Pageable;

import java.awt.image.renderable.ParameterBlock;
import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface ReadingRepository  extends MongoRepository<Reading, ObjectId> {
    void deleteAllByComic(Comic comic);
    Page<Reading> findByUser(User user, Pageable pageable);
    Reading findByComicAndUser(Comic comic, User user);

    @Query("{'user._id':?0, 'comic._id':?1}")
    Optional<Reading> findWithParam(ObjectId userId, ObjectId novelId);
    @Query("{}")
    List<Reading> find(Pageable pageable);

}