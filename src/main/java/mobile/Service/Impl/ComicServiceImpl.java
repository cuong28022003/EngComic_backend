package mobile.Service.Impl;

import mobile.Service.*;

import mobile.mapping.ComicMapping;
import mobile.model.Entity.Comic;
import mobile.model.payload.response.comic.ComicResponse;
import mobile.repository.comic.ComicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ComicServiceImpl implements ComicService {
    private final UserRepository userRepository;
    final ComicRepository comicRepository;
    private final ComicMapping comicMapping;
    private final CloudinaryService cloudinaryService;
    private final ChapterService chapterService;
    private final SavedService savedService;
    private final ReadingService readingService;
    private final RatingService ratingService;

    @Override
    public Page<ComicResponse> getComicsAdmin(Pageable pageable) {
        Page<Comic> comics = comicRepository.findAll(pageable);
        return comics.map(comic -> comicMapping.toComicResponse(comic));
    }

    @Override
    public Page<ComicResponse> getComics(Pageable pageable) {
        log.info("Fetching all Comics with status != 'LOCK', page: " + pageable.getPageNumber() + " page size: "
                + pageable.getPageSize());
        Page<Comic> comics = comicRepository.findAllByStatusNot("LOCK", pageable);
        return comics.map(comic -> comicMapping.toComicResponse(comic));
    }


    @Override
    public void incrementViews(ObjectId id) {
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comic not found with id: " + id));
        comic.setViews(comic.getViews() + 1);
        comicRepository.save(comic);
    }

    @Override
    public ComicResponse create(String name, String url, String description, String genre, String artist, ObjectId uploaderId, MultipartFile image, MultipartFile backgroundImage) {
        log.info("Creating new Comic with name: " + name);
        Comic comic = new Comic();
        comic.setName(name);
        comic.setUrl(url);
        comic.setDescription(description);
        comic.setGenre(genre);
        comic.setArtist(artist);
        comic.setUploaderId(uploaderId);
        comic.setViews(0);
        comic.setStatus("NONE");

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(image);
                comic.setImageUrl(imageUrl);
                log.info("Image uploaded successfully for comic: " + name);
            } catch (Exception e) {
                log.error("Error saving image for comic: " + name, e);
                throw new RuntimeException("Failed to save image for comic: " + name);
            }
        }
        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            try {
                String backgroundUrl = cloudinaryService.uploadFile(backgroundImage);
                comic.setBackgroundUrl(backgroundUrl);
                log.info("Background image uploaded successfully for comic: " + name);
            } catch (Exception e) {
                log.error("Error saving background image for comic: " + name, e);
                throw new RuntimeException("Failed to save background image for comic: " + name);
            }
        }

        return comicMapping.toComicResponse(comicRepository.save(comic));
    }

    @Override
    public ComicResponse update(ObjectId id, String name, String url, String description, String genre, String artist, ObjectId uploaderId, MultipartFile image, MultipartFile backgroundImage) {
        log.info("Updating Comic with id: " + id);
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comic not found with id: " + id));

        comic.setName(name);
        comic.setUrl(url);
        comic.setDescription(description);
        comic.setGenre(genre);
        comic.setArtist(artist);
        comic.setUploaderId(uploaderId);

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(image);
                comic.setImageUrl(imageUrl);
                log.info("Image updated successfully for comic: " + name);
            } catch (Exception e) {
                log.error("Error updating image for comic: " + name, e);
                throw new RuntimeException("Failed to update image for comic: " + name);
            }
        }
        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            try {
                String backgroundUrl = cloudinaryService.uploadFile(backgroundImage);
                comic.setBackgroundUrl(backgroundUrl);
                log.info("Background image updated successfully for comic: " + name);
            } catch (Exception e) {
                log.error("Error updating background image for comic: " + name, e);
                throw new RuntimeException("Failed to update background image for comic: " + name);
            }
        }

        return comicMapping.toComicResponse(comicRepository.save(comic));
    }

    @Override
    public Page<ComicResponse> searchComics(String keyword, String genre, String artist, ObjectId uploaderId, String sortBy, String sortDir, Pageable pageable) {
        Page<Comic> comics = comicRepository.searchComics(keyword, genre, artist, uploaderId, sortBy, sortDir, pageable);
        return  comics.map(comic -> comicMapping.toComicResponse(comic));
    }

    @Override
    public void deleteById(ObjectId id) {
        log.info("Deleting Comic with id: " + id);
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comic not found with id: " + id));
        ratingService.deleteAllRatingsByComicId(id);
        readingService.deleteAllReadingByComicId(id);
        savedService.deleteAllByComicId(id);
        chapterService.deleteAllByComicId(id);
        comicRepository.deleteById(id);
        log.info("Comic deleted successfully with id: " + id);
    }

    @Override
    public ComicResponse findById(ObjectId id) {
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comic not found with id: " + id));
        return comicMapping.toComicResponse(comic);
    }
}
