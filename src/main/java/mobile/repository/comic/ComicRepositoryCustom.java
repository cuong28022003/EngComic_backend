package mobile.repository.comic;

import mobile.model.Entity.Comic;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ComicRepositoryCustom {
    Page<Comic> searchComics(String keyword, String genre, String artist, ObjectId uploaderId, String sortBy, String sortDir, Pageable pageable);
}
