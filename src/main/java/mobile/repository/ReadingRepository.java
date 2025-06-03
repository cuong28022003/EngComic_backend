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
    @Query("{ 'userId': ?0 }")
    Page<Reading> findByUserId(ObjectId userId, Pageable pageable);

    @Query("{ 'comicId': ?0 }")
    List<Reading> findByComicId(ObjectId comicId);

    @Query("{ 'userId': ?0, 'comicId': ?1 }")
    Optional<Reading> findByUserIdAndComicId(ObjectId userId, ObjectId comicId);

    void deleteAllByComicId(ObjectId comicId);

}