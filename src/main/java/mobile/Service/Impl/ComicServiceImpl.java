package mobile.Service.Impl;

import mobile.Service.ComicService;

import mobile.mapping.ComicMapping;
import mobile.model.Entity.Comic;
import mobile.model.Entity.User;
import mobile.model.payload.response.ComicResponse;
import mobile.repository.ComicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final UserRepository userRepository;
    final ComicRepository comicRepository;
    private final ComicMapping comicMapping;

    @Override
    public Page<Comic> getComics(Pageable pageable) {
        log.info("Fetching all Comics with status != 'LOCK', page: " + pageable.getPageNumber() + " page size: "
                + pageable.getPageSize());
        return comicRepository.findAllByStatusNot("LOCK", pageable);
    }

    @Override
    public Page<Comic> findByName(String value, Pageable pageable) {
        log.info("Searching  Novel value: " + value);
        return comicRepository.findByNameContainingIgnoreCase(value, pageable);
    }

    @Override
    public Page<Comic> findByGenre(String value, Pageable pageable) {
        log.info("Searching Novel by theloai: " + value);
        return comicRepository.findByGenreContainingIgnoreCase(value, pageable);
    }

    @Override
    public Page<Comic> findByArtist(String value, Pageable pageable) {
        log.info("Searching Novel value: " + value);
        return comicRepository.findByArtistContainingIgnoreCase(value, pageable);
    }

    @Override
    public Page<Comic> findByUploaderId(ObjectId userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return comicRepository.findByUploader(user, pageable);
    }

    @Override
    public Comic findByUrl(String url) {
        log.info("Fetching  Novel: " + url);
        return comicRepository.findByUrl(url);
    }

    @Override
    public Optional<Comic> findComicById(ObjectId id) {
        return comicRepository.findBy_id(id);
    }

    @Override
    public void saveComic(Comic newComic) {
        comicRepository.save(newComic);
    }

    @Override
    public List<Comic> getAllComics() {
        log.info("Fetching all Novels ");
        return comicRepository.findAll();
    }

    @Override
    public List<Comic> findAllByStatus(String status, Pageable pageable) {
        log.info("Fetching  Novel status: " + status);
        return comicRepository.findAllByStatus(status, pageable);
    }

    @Override
    public List<Comic> SearchByTypeAndName(String type, String value, Pageable pageable) {
        log.info("Searching  Novel type: " + type + " value: " + value);
        return comicRepository.findAllByGenreContainsAndNameContainsAllIgnoreCase(type, value, pageable);
    }

    @Override
    public void DeleteComic(Comic comic) {
        comicRepository.delete(comic);
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

    @Override
    public ComicResponse findById(ObjectId id) {
        Comic comic = comicRepository.findBy_id(id)
                .orElseThrow(() -> new RuntimeException("Comic not found with id: " + id));
        return comicMapping.toComicResponse(comic);
    }
}
