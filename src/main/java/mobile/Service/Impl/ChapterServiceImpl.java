package mobile.Service.Impl;

import mobile.Service.ChapterService;
import mobile.model.Entity.Chapter;
import mobile.model.Entity.Comic;
import mobile.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class ChapterServiceImpl implements ChapterService {
     final ChapterRepository chapterRepository;

    @Override
    public Page<Chapter> findByComic(Comic comic, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("chapterNumber"));
        log.info("Fetching all chapter  Novel id: "+comic+" Page: "+page+" Size "+size);
        return chapterRepository.findByComic(comic, pageable);
    }

    @Override
    public List<Chapter> findAllByComic(Comic comic) {
        return  chapterRepository.findAllByComic(comic, Sort.by("chapterNumber"));
    }

    @Override
    public Chapter findById(String chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + chapterId));
    }

    @Override
    public int countChaptersByComic(Comic comic) {
        return chapterRepository.countAllByComic(comic);
    }

    @Override
    public Chapter findByComicAndChapterNumber(Comic comic, int chapterNumber) {
        return chapterRepository.findByComicAndChapterNumber(comic, chapterNumber).get();
    }


    @Override
    public List<Object> getNameAndChapnumber(ObjectId id,Pageable pageable) {
        log.info("fetch all chapter  Novel id: "+id.toHexString());
        return null;
    }

    @Override
    public void DeleteAllChapterByComic(Comic comic) {
        chapterRepository.deleteAllByComic(comic);
    }

    @Override
    public void SaveChapter(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    @Override
    public void DeleteChapter(Chapter chapter) {
        chapterRepository.delete(chapter);
    }
}
