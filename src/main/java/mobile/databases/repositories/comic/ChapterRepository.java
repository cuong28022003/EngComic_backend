package mobile.databases.repositories.comic;

import mobile.databases.entities.comic.ChapterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("chapterEntityRepository")
public interface ChapterRepository extends MongoRepository<ChapterEntity, String> {
    Page<ChapterEntity> findByComicId(String comicId, Pageable pageable);
    List<ChapterEntity> findByComicIdOrderByChapterNumberAsc(String comicId);
    Optional<ChapterEntity> findByComicIdAndChapterNumber(String comicId, int chapterNumber);
    void deleteAllByComicId(String comicId);
    long countByComicId(String comicId);
}

