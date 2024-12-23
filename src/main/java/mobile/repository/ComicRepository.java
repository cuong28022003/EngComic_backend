package mobile.repository;

import mobile.model.Entity.Comic;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface ComicRepository extends MongoRepository<Comic, ObjectId> {
    Optional<Comic> findByName(String name);

    Optional<Comic> findBy_id(ObjectId id);

    Comic findByUrl(String url);

    List<Comic> findAllBy_idNotNull(Pageable pageable);

    List<Comic> findAllByStatus(String status, Pageable pageable);

    List<Comic> findAllByNameContainsAllIgnoreCase(String value, Pageable pageable);

    List<Comic> findAllByGenreContainsAndNameContainsAllIgnoreCase(String type, String value, Pageable pageable);

    List<Comic> findAllByArtistContainsAllIgnoreCase(String value, Pageable pageable);

    List<Comic> findAllByGenreContainsAllIgnoreCase(String theloai, Pageable pageable);

    List<Comic> findByNameLike(String name);

    List<Comic> findByGenre(String genre);

    List<Comic> findByArtist(String artist);

    List<Comic> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Comic> findByNameContainingIgnoreCaseAndGenreContainingIgnoreCase(String name, String genre,
            Pageable pageable);

    @Query("{'uploader.$id':?0}")
    List<Comic> findWithUserId(ObjectId id, Pageable pageable);

    @Query("{'uploader': ?0}")
    List<Comic> findByUploaderContaining(ObjectId userId);
}
