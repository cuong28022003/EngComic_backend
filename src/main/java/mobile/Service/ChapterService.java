package mobile.Service;

import mobile.model.Entity.Chapter;
import mobile.model.Entity.Comic;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChapterService {
    Page<Chapter> findByComic(Comic comic, int page, int size);
    List<Chapter> findAllByComic(Comic comic);
    Chapter findById(String chapterId);
    int countChaptersByComic(Comic comic);


    Chapter findByComicAndChapterNumber(Comic comic, int chapterNumber);
    List<Object> getNameAndChapnumber(ObjectId id, Pageable pageable);
    void DeleteAllChapterByComic(Comic comic);
    void SaveChapter(Chapter chapter);
    void DeleteChapter(Chapter chapter);
}
