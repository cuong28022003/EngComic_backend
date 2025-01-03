package mobile.Service;

import mobile.model.Entity.Chapter;
import mobile.model.Entity.Comic;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChapterService {
    List<Chapter> findByDauTruyen(ObjectId id);
    Chapter findByDauTruyenAndChapterNumber(ObjectId id,int number);
    List<Chapter> findByDauTruyen(ObjectId id, Pageable pageable);
    int countByDauTruyen(ObjectId id);
    List<Object> getNameAndChapnumber(ObjectId id, Pageable pageable);
    void DeleteAllChapterByComic(Comic comic);
    List<Chapter> getChaptersNewUpdate(Pageable pageable);
    void SaveChapter(Chapter chapter);
    void DeleteChapter(Chapter chapter);
}
