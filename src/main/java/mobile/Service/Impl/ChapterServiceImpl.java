package mobile.Service.Impl;

import mobile.Service.ChapterService;
import mobile.Service.CloudinaryService;
import mobile.mapping.ChapterMapping;
import mobile.model.Entity.Chapter;
import mobile.model.Entity.Comic;
import mobile.model.payload.response.chapter.ChapterResponse;
import mobile.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.repository.comic.ComicRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChapterServiceImpl implements ChapterService {
    final ChapterRepository chapterRepository;
    final ComicRepository comicRepository;
    private final ChapterMapping chapterMapping;
    private final CloudinaryService cloudinaryService;


    @Override
    public Page<ChapterResponse> findByComicId(ObjectId comicId, Pageable pageable) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new IllegalArgumentException("Comic not found with id: " + comicId));
        Page<Chapter> chapters = chapterRepository.findByComicId(comicId, pageable);
        if (chapters.isEmpty()) {
            return Page.empty();
        }
        return chapters.map(chapter -> chapterMapping.toChapterResponse(chapter));
    }

    @Override
    public ChapterResponse findById(ObjectId chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));
        return chapterMapping.toChapterResponse(chapter);
    }

    @Override
    public ChapterResponse findByComicIdAndChapterNumber(ObjectId comicId, Integer chapterNumber) {
        Chapter chapter = chapterRepository.findByComicIdAndChapterNumber(comicId, chapterNumber)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found for comic with id: " + comicId + " and chapter number: " + chapterNumber));
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new IllegalArgumentException("Comic not found with id: " + comicId));
        comic.setViews(comic.getViews() + 1);
        comicRepository.save(comic);
        return chapterMapping.toChapterResponse(chapter);
    }

    @Override
    public ChapterResponse createChapter(String name, Integer chapterNumber, ObjectId comicId, MultipartFile[] pages, MultipartFile image) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new IllegalArgumentException("Comic not found with id: " + comicId));

        if (chapterRepository.findByComicIdAndChapterNumber(comicId, chapterNumber).isPresent()) {
            throw new IllegalArgumentException("Chapter number already exists for this comic");
        }

        Chapter chapter = new Chapter();
        chapter.setName(name);
        chapter.setChapterNumber(chapterNumber);
        chapter.setComicId(comicId);

        // Upload image
        if (image != null) {
            try {
                String imageUrl = cloudinaryService.uploadFile(image);
                chapter.setImageUrl(imageUrl);
            } catch (Exception e) {
                log.error("Error uploading image: {}", e.getMessage());
                throw new RuntimeException("Failed to upload chapter image");
            }
        }

        // Upload pages
        if (pages != null && pages.length > 0) {
            try {
                List<Map<Integer, String>> pageList = new ArrayList<>();
                for (MultipartFile page : pages) {
                    String fileName = page.getOriginalFilename();
                    if (fileName == null || fileName.isEmpty()) {
                        throw new IllegalArgumentException("File name is invalid");
                    }

                    // Extract order from file name (e.g., "1.png" -> 1)
                    int order = Integer.parseInt(fileName.split("\\.")[0]);
                    String pageUrl = cloudinaryService.uploadFile(page);

                    // Add the page with its order and URL
                    pageList.add(Map.of(order, pageUrl));
                }
                chapter.setPageUrls(pageList);
            } catch (Exception e) {
                log.error("Error uploading pages: {}", e.getMessage());
                throw new RuntimeException("Failed to upload chapter pages");
            }
        }

        chapterRepository.save(chapter);
        return chapterMapping.toChapterResponse(chapter);
    }

    @Override
    public ChapterResponse updateChapter(ObjectId chapterId, String name, Integer chapterNumber, ObjectId comicId, MultipartFile[] pages, MultipartFile image) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));

        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new IllegalArgumentException("Comic not found with id: " + comicId));

        if (chapterRepository.findByComicIdAndChapterNumber(comicId, chapterNumber).isPresent() &&
                chapter.getChapterNumber() != chapterNumber) {
            throw new IllegalArgumentException("Chapter number already exists for this comic");
        }

        chapter.setName(name);
        chapter.setChapterNumber(chapterNumber);
        chapter.setComicId(comicId);

        // Update image
        if (image != null) {
            try {
                String imageUrl = cloudinaryService.uploadFile(image);
                chapter.setImageUrl(imageUrl);
            } catch (Exception e) {
                log.error("Error uploading image: {}", e.getMessage());
                throw new RuntimeException("Failed to upload chapter image");
            }
        }

        // Update pages
        if (pages != null && pages.length > 0) {
            try {
                if (chapter.getPageUrls() == null) {
                    chapter.setPageUrls(new ArrayList<>());
                }

                for (MultipartFile page : pages) {
                    String fileName = page.getOriginalFilename();
                    if (fileName == null || fileName.isEmpty()) {
                        throw new IllegalArgumentException("File name is invalid");
                    }

                    // Extract order from file name (e.g., "1.png" -> 1)
                    int order = Integer.parseInt(fileName.split("\\.")[0]);
                    String pageUrl = cloudinaryService.uploadFile(page);

                    // Check if the order already exists
                    boolean updated = false;
                    for (Map<Integer, String> existingPage : chapter.getPageUrls()) {
                        if (existingPage.containsKey(order)) {
                            existingPage.put(order, pageUrl); // Update the URL
                            updated = true;
                            break;
                        }
                    }

                    // If not updated, add a new page
                    if (!updated) {
                        chapter.getPageUrls().add(Map.of(order, pageUrl));
                    }
                }
            } catch (Exception e) {
                log.error("Error uploading pages: {}", e.getMessage());
                throw new RuntimeException("Failed to upload chapter pages");
            }
        }

        chapterRepository.save(chapter);
        return chapterMapping.toChapterResponse(chapter);
    }

    @Override
    public void deleteChapter(ObjectId chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));

        // Delete chapter image
        if (chapter.getImageUrl() != null) {
            try {
                cloudinaryService.deleteFile(chapter.getImageUrl());
            } catch (Exception e) {
                log.error("Error deleting chapter image: {}", e.getMessage());
                throw new RuntimeException("Failed to delete chapter image");
            }
        }

        // Delete chapter pages
        if (chapter.getPageUrls() != null && !chapter.getPageUrls().isEmpty()) {
            for (Map<Integer, String> page : chapter.getPageUrls()) {
                for (String pageUrl : page.values()) {
                    try {
                        cloudinaryService.deleteFile(pageUrl);
                    } catch (Exception e) {
                        log.error("Error deleting page image: {}", e.getMessage());
                        throw new RuntimeException("Failed to delete page image");
                    }
                }
            }
        }

        // Delete chapter from database
        chapterRepository.deleteById(chapterId);
    }

    @Override
    public void deleteAllByComicId(ObjectId comicId) {
        List<Chapter> chapters = chapterRepository.findAllByComicId(comicId);
        for (Chapter chapter : chapters) {
            deleteChapter(chapter.getId());
        }
    }

    @Override
    public int countChaptersByComicId(ObjectId comicId) {
        return chapterRepository.countChaptersByComicId(comicId);
    }
}
