package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Handler.RecordNotFoundException;
import mobile.Service.*;
import mobile.mapping.ChapterMapping;
import mobile.model.Entity.Chapter;
import mobile.model.Entity.Comic;
import mobile.model.Entity.User;
import mobile.model.payload.response.comic.ComicResponse;
import mobile.model.payload.response.chapter.ChapterResponse;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("api/chapter")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;
    private final ComicService comicService;
    private final UserService userService;
    private final ReadingService readingService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    JwtUtils jwtUtils;

//    @GetMapping("/all")
//    public ResponseEntity<List<ChapterResponse>> getAllChaptersByComic(@RequestParam String url) {
//        Comic comic = comicService.findByUrl(url);
//        if (comic == null) {
//            throw new RecordNotFoundException("Không tìm thấy truyện với URL: " + url);
//        }
//
//        List<Chapter> chapters = chapterService.findAllByComic(comic);
//        if (chapters.isEmpty()) {
//            throw new RecordNotFoundException("Không có chương nào được đăng");
//        }
//
//        // Chuyển đổi danh sách Chapter sang ChapterResponse
//        List<ChapterResponse> chapterResponses = chapters.stream()
//                .map(chapter -> ChapterMapping.toChapterResponse(chapter, chapters.size()))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(chapterResponses);
//    }

    @GetMapping("/comic/{comicId}")
    public ResponseEntity<Page<ChapterResponse>> getChaptersByComicId(@PathVariable String comicId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

       Page<ChapterResponse> chapterResponses = chapterService.findByComicId(new ObjectId(comicId), PageRequest.of(page, size));

        return ResponseEntity.ok(chapterResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponse> getChapter(@PathVariable String id) {
        ChapterResponse chapterResponse = chapterService.findById(new ObjectId(id));
        return ResponseEntity.ok(chapterResponse);
    }

    @GetMapping
    public ResponseEntity<ChapterResponse> getChapterByComicIdAndChapterNumber(@RequestParam String comicId,
            @RequestParam int chapterNumber) {
        ChapterResponse chapterResponse = chapterService.findByComicIdAndChapterNumber(new ObjectId(comicId), chapterNumber);
        return ResponseEntity.ok(chapterResponse);
    }

    @PostMapping("")
    public ResponseEntity<ChapterResponse> CreateChapter(
            @RequestParam("name") String name,
            @RequestParam("comicId") String comicId,
            @RequestParam("chapterNumber") int chapterNumber,
            @RequestParam("pages") MultipartFile[] pages,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

        ComicResponse comic = comicService.findById(new ObjectId(comicId));

        if (!comic.getUploaderId().equals(user.getId().toHexString())) {
            throw new RuntimeException("Unauthorized access");
        }

        ChapterResponse chapterResponse = chapterService.createChapter(name, chapterNumber, new ObjectId(comicId), pages, image);
        return ResponseEntity.ok(chapterResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChapterResponse> updateChapter(
            @PathVariable String id,
            @RequestParam (value = "name", required = false) String name,
            @RequestParam(value = "comicId", required = false) String comicId,
            @RequestParam (value = "chapterNumber", required = false) Integer chapterNumber,
            @RequestParam(value = "pages", required = false) MultipartFile[] pages,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
        ComicResponse comic = comicService.findById(new ObjectId(comicId));
        if (!comic.getUploaderId().equals(user.getId().toHexString())) {
            throw new RuntimeException("Unauthorized access");
        }

        ChapterResponse chapterResponse = chapterService.updateChapter(new ObjectId(id), name, chapterNumber, new ObjectId(comicId), pages, image);

        return ResponseEntity.ok(chapterResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteChapter(@PathVariable String id,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

        chapterService.deleteChapter(new ObjectId(id));
        return ResponseEntity.ok("Chapter deleted successfully");
    }

}
