package mobile.Service;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Saved;
import mobile.model.payload.response.SavedResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SavedService {
    Page<SavedResponse> getByUserId(ObjectId userId, Pageable pageable);
    SavedResponse create(ObjectId userId, ObjectId comicId);
    void delete(ObjectId id);
    SavedResponse getById(ObjectId id);

    SavedResponse getByUserIdAndComicId(ObjectId userId, ObjectId comicId);

    void deleteAllByComicId(ObjectId comicId);
}
