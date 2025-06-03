package mobile.repository;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Saved;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface SavedRepository extends MongoRepository<Saved, ObjectId> {
    Page<Saved> findByUserId(ObjectId userId, Pageable pageable);
    List<Saved> findAllByComicId(ObjectId userId);
    Optional<Saved> findByUserIdAndComicId(ObjectId userId, ObjectId comicId);
}
