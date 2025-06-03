package mobile.Service;

import mobile.model.Entity.Comic;
import mobile.model.payload.response.comic.ComicResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ComicService {
    Page<ComicResponse> searchComics(String keyword, String genre, String artist, ObjectId uploaderId, String sortBy, String sortDir, Pageable pageable);
    void deleteById(ObjectId id);
    ComicResponse findById(ObjectId id);
    //fixed
    ComicResponse create(String name, String url, String description, String genre, String artist, ObjectId uploaderId, MultipartFile image);
    ComicResponse update(ObjectId id, String name, String url, String description, String genre, String artist, ObjectId uploaderId, MultipartFile image);

    Page<ComicResponse> getComics(Pageable pageable);

    void incrementViews(ObjectId id);
}
