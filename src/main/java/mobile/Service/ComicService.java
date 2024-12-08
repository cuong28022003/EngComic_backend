package mobile.Service;

import mobile.model.Entity.Comic;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ComicService {
    List<Comic> getComics();
    Comic findByName(String name);
    Comic findByUrl(String url);
    List<Comic> getComics(Pageable pageable);
    List<Comic> findAllByStatus(String status, Pageable pageable);
    List<Comic> SearchByName(String value, Pageable pageable);
    List<Comic> SearchByNameAndGenre(String name, String genre, Pageable pageable);
    List<Comic> SearchByTypeAndName(String type, String value, Pageable pageable);
    List<Comic> SearchByArtist(String value, Pageable pageable);
    List<Comic> SearchByGenre(String genre, Pageable pageable);
    List<Comic> SearchByUploader(ObjectId id, Pageable pageable);
    void SaveComic(Comic newComic);
    Optional<Comic> findById(ObjectId id);
    void DeleteComic(Comic comic);
    List<Comic> findByNameLike(String name);
    List<Comic> getComicsByGenre(String genre);
    List<Comic> getComicsByArtist(String artist);

    Comic incrementViews(String url);
}
