package mobile.Service.Impl;

import mobile.Service.ComicService;

import mobile.model.Entity.Comic;
import mobile.repository.ComicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ComicServiceImpl implements ComicService {
    final ComicRepository comicRepository;

    @Override
    public List<Comic> getAllComics() {
        log.info("Fetching all Novels ");
        return comicRepository.findAll();
    }

    @Override
    public List<Comic> getComics(Pageable pageable) {
        log.info("Fetching all Novels page: " + pageable.getPageNumber() + " page size: " + pageable.getPageSize());
        return comicRepository.findAllBy_idNotNull(pageable);
    }

    @Override
    public Comic findByName(String name) {
        log.info("Fetching  Novel: " + name);
        return comicRepository.findByName(name).get();
    }

    @Override
    public Comic findByUrl(String url) {
        log.info("Fetching  Novel: " + url);
        return comicRepository.findByUrl(url);
    }

    @Override
    public List<Comic> findAllByStatus(String status, Pageable pageable) {
        log.info("Fetching  Novel status: " + status);
        return comicRepository.findAllByStatus(status, pageable);
    }

    @Override
    public List<Comic> SearchByName(String value, Pageable pageable) {
        log.info("Searching  Novel value: " + value);
        return comicRepository.findAllByNameContainsAllIgnoreCase(value, pageable);
    }

    @Override
    public List<Comic> SearchByTypeAndName(String type, String value, Pageable pageable) {
        log.info("Searching  Novel type: " + type + " value: " + value);
        return comicRepository.findAllByGenreContainsAndNameContainsAllIgnoreCase(type, value, pageable);
    }

    @Override
    public List<Comic> SearchByArtist(String value, Pageable pageable) {
        log.info("Searching Novel value: " + value);
        return comicRepository.findAllByArtistContainsAllIgnoreCase(value, pageable);
    }

    @Override
    public List<Comic> SearchByGenre(String genre, Pageable pageable) {
        log.info("Searching Novel by theloai: " + genre);
        return comicRepository.findAllByGenreContainsAllIgnoreCase(genre, pageable);
    }

    @Override
    public List<Comic> SearchByUploader(ObjectId id, Pageable pageable) {
        return comicRepository.findWithUserId(id, pageable);
    }

    @Override
    public void SaveComic(Comic newComic) {
        comicRepository.save(newComic);
    }

    @Override
    public Optional<Comic> findById(ObjectId id) {
        return comicRepository.findById(id);
    }

    @Override
    public void DeleteComic(Comic comic) {
        comicRepository.delete(comic);
    }

    @Override
    public List<Comic> findByNameLike(String name) {
        log.info("Searching Novel by firstname like: " + name);
        return comicRepository.findByNameLike(name);
    }

    public List<Comic> findByUploaderContaining(ObjectId id) {
        return comicRepository.findByUploaderContaining(id);
    }

    @Override
    public List<Comic> getComicsByGenre(String genre) {
        return comicRepository.findByGenre(genre);
    }

    @Override
    public List<Comic> getComicsByArtist(String artist) {
        return comicRepository.findByArtist(artist);
    }

    @Override
    public List<Comic> SearchByNameAndGenre(String name, String genre, Pageable pageable) {
        return comicRepository.findByNameContainingIgnoreCaseAndGenreContainingIgnoreCase(name, genre, pageable);
    }

    @Override
    public Comic incrementViews(String url) {
        Comic comic = comicRepository.findByUrl(url);
        comic.setViews(comic.getViews() + 1);
        return comicRepository.save(comic);
    }

    @Override
    public boolean deleteComicByUrl(String url) {
        Comic comic = comicRepository.findByUrl(url);
        if (comic != null) {
            comicRepository.delete(comic);
            return true;
        }
        return false;
    }
}
