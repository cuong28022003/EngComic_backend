package mobile.repository;

import mobile.model.Entity.Chapter;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface ChapterRepository  extends MongoRepository<Chapter, ObjectId> {
    Page<Chapter> findByComicId(ObjectId comicId, Pageable pageable);

    List<Chapter> findAllByComicId(ObjectId comicId);

    Optional<Chapter> findById(ObjectId chapterId);


    int countChaptersByComicId(ObjectId comicId);

    Optional<Chapter> findByComicIdAndChapterNumber(ObjectId comicId, int chapterNumber);

    void deleteAllByComicId(ObjectId comicId);
}