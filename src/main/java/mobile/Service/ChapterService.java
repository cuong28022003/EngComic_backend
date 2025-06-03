package mobile.Service;

import mobile.model.payload.response.chapter.ChapterResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ChapterService {
    Page<ChapterResponse> findByComicId(ObjectId comicId, Pageable pageable);
    ChapterResponse findById(ObjectId chapterId);
    ChapterResponse findByComicIdAndChapterNumber(ObjectId comicId, Integer chapterNumber);
    ChapterResponse createChapter(String name, Integer chapterNumber, ObjectId comicId, MultipartFile[] pages, MultipartFile image);
    ChapterResponse updateChapter(ObjectId chapterId, String name, Integer chapterNumber, ObjectId comicId, MultipartFile[] pages, MultipartFile image);
    void deleteChapter(ObjectId chapterId);
    void deleteAllByComicId(ObjectId comicId);

//    void SaveChapter(Chapter chapter);
//    List<Chapter> findAllByComic(Comic comic);
    int countChaptersByComicId(ObjectId comicId);
//
//
//    Chapter findByComicAndChapterNumber(Comic comic, int chapterNumber);
//    List<Object> getNameAndChapnumber(ObjectId id, Pageable pageable);
//    void DeleteAllChapterByComic(Comic comic);
}
