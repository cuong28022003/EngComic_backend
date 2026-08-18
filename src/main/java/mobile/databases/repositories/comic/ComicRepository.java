package mobile.databases.repositories.comic;

import mobile.databases.entities.comic.ComicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("comicEntityRepository")
public interface ComicRepository extends MongoRepository<ComicEntity, String> {
    Optional<ComicEntity> findByUrl(String url);
    Optional<ComicEntity> findByName(String name);
    Page<ComicEntity> findByStatus(String status, Pageable pageable);
    Page<ComicEntity> findByUploaderId(String uploaderId, Pageable pageable);
}
