package mobile.repository.comic;

import mobile.model.Entity.Comic;
import mobile.model.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface ComicRepository extends MongoRepository<Comic, ObjectId>, ComicRepositoryCustom {
    Page<Comic> findAll(Pageable pageable);
//    Page<Comic> findAllBy_idNotNull(Pageable pageable);
//
//    Page<Comic> findByNameContainingIgnoreCase(String value, Pageable pageable);
//
//    Page<Comic> findByGenreContainingIgnoreCase(String value, Pageable pageable);
//
//    Page<Comic> findByArtistContainingIgnoreCase(String value, Pageable pageable);
//
//    Comic findByUrl(String url);
//
//    Optional<Comic> findBy_id(ObjectId id);
//
//    Page<Comic> findByUploader(User user, Pageable pageable);
//
    Page<Comic> findAllByStatusNot(String status, Pageable pageable);
//
//    List<Comic> findAllByStatus(String status, Pageable pageable);
//
//    List<Comic> findAllByGenreContainsAndNameContainsAllIgnoreCase(String type, String value, Pageable pageable);
//
//    List<Comic> findAllByArtistContainsAllIgnoreCase(String value, Pageable pageable);
//
//    List<Comic> findAllByGenreContainsAllIgnoreCase(String theloai, Pageable pageable);
//
//    List<Comic> findByNameLike(String name);
//
//    List<Comic> findByGenre(String genre);
//
//    List<Comic> findByArtist(String artist);
//
//    List<Comic> findByNameContainingIgnoreCaseAndGenreContainingIgnoreCase(String name, String genre,
//            Pageable pageable);

}
