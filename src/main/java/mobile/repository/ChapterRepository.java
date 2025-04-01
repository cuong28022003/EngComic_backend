package mobile.repository;

import mobile.model.Entity.Chapter;
import mobile.model.Entity.Comic;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface ChapterRepository  extends MongoRepository<Chapter, String> {
    Optional<Chapter> findById(String chapterId);

    Page<Chapter> findByComic(Comic comic, Pageable pageable);
    Optional<Chapter> findByComicAndChapterNumber(Comic comic, int chapterNumber);
    int countAllByComic(Comic comic);
    void deleteAllByComic(Comic comic);
}