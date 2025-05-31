package mobile.Service;

import mobile.model.Entity.Comic;
import mobile.model.payload.response.ComicResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ComicService {
    Page<Comic> getComics(Pageable pageable);

    Page<Comic> findByName(String name, Pageable pageable);

    Page<Comic> findByArtist(String value, Pageable pageable);

    Page<Comic> findByGenre(String genre, Pageable pageable);

    Page<Comic> findByUploaderId(ObjectId userId, Pageable pageable);

    Comic findByUrl(String url);

    Optional<Comic> findComicById(ObjectId id);

    void saveComic(Comic newComic);



    List<Comic> getAllComics();


    List<Comic> findAllByStatus(String status, Pageable pageable);


    List<Comic> SearchByNameAndGenre(String name, String genre, Pageable pageable);

    List<Comic> SearchByTypeAndName(String type, String value, Pageable pageable);





    void DeleteComic(Comic comic);

    Comic incrementViews(String url);

    boolean deleteComicByUrl(String url);

//    List<Comic> findByUploaderContaining(ObjectId id);

    ComicResponse findById(ObjectId id);
}
