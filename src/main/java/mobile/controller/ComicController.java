package mobile.controller;

import mobile.Service.*;
import mobile.mapping.ComicMapping;
import mobile.model.Entity.*;
import mobile.model.payload.request.novel.CreateComicRequest;
import mobile.model.payload.response.*;
import mobile.model.payload.response.comic.ComicResponse;
import mobile.repository.comic.ComicRepository;
import mobile.security.JWT.JwtUtils;
import mobile.Handler.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import com.cloudinary.Cloudinary;
import org.springframework.web.multipart.MultipartFile;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/comic")
@RequiredArgsConstructor
public class ComicController {
    private static final Logger LOGGER = LogManager.getLogger(ComicController.class);

    private final UserService userService;
    private final ComicService comicService;
    private final ChapterService chapterService;
    private final ReadingService readingService;
    private final SavedService savedService;
    private final RatingService ratingService;
    private final ComicMapping comicMapping;
    private final CloudinaryService cloudinaryService;
    private final ComicRepository comicRepository;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("")
    @ResponseBody
    public ResponseEntity<Page<ComicResponse>> getComics(@RequestParam(defaultValue = "None") String status,
                                                         @RequestParam(defaultValue = "name") String sort,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "desc") String order,
                                                         @RequestParam(defaultValue = "3") int size) {
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<ComicResponse> comicPage = comicService.getComics(pageable);
        return ResponseEntity.ok(comicPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComicResponse> getComicById(@PathVariable String id) {
        ObjectId comicId = new ObjectId(id);
        ComicResponse comicResponse = comicService.findById(comicId);
        if (comicResponse == null) {
            throw new RecordNotFoundException("Comic not found with id: " + id);
        }
        return ResponseEntity.ok(comicResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ComicResponse>> searchComic(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String uploaderId,
            @RequestParam(defaultValue = "views") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        // xử lý trường hợp uploaderId = null -> k chuyển qua ObjectId đuược
        ObjectId uploaderObjectId = null;
        if (uploaderId != null && !uploaderId.isBlank()) {
            try {
                uploaderObjectId = new ObjectId(uploaderId); // chỉ khi hợp lệ
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Page.empty()); // hoặc throw custom exception
            }
        }
        Page<ComicResponse> comicPage = comicService.searchComics(keyword, genre, artist, uploaderObjectId, sortBy, sortDir, pageable);
        return ResponseEntity.ok(comicPage);
    }

    @PostMapping("")
    @ResponseBody
    public ResponseEntity<ComicResponse> createComic(@RequestPart("data") CreateComicRequest createComicRequest,
            @RequestPart("image") MultipartFile image,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        String name = createComicRequest.getName();
        String url = createComicRequest.getUrl();
        String description = createComicRequest.getDescription();
        String genre = createComicRequest.getGenre();
        String artist = createComicRequest.getArtist();
        ObjectId uploaderId = new ObjectId(createComicRequest.getUploaderId());

        ComicResponse comicResponse = comicService.create(name, url, description, genre, artist, uploaderId, image);
        return ResponseEntity.ok(comicResponse);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ComicResponse> updateComic(@PathVariable String id,
                                                     @RequestPart("data") CreateComicRequest updateComicRequest,
                                                        @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId comicId = new ObjectId(id);
        String name = updateComicRequest.getName();
        String url = updateComicRequest.getUrl();
        String description = updateComicRequest.getDescription();
        String genre = updateComicRequest.getGenre();
        String artist = updateComicRequest.getArtist();
        ObjectId uploaderId = new ObjectId(updateComicRequest.getUploaderId());

        ComicResponse comicResponse = comicService.update(comicId, name, url, description, genre, artist, uploaderId, image);

        return ResponseEntity.ok(comicResponse);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComic(@PathVariable String id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

        ComicResponse comic = comicService.findById(new ObjectId(id));

//        if (!comic.getUploaderId().equals(user.getId().toHexString())) {
//            throw new RuntimeException("Unauthorized access");
//        }

        comicService.deleteById(new ObjectId(id));

        return ResponseEntity.ok("Comic deleted successfully");
    }

    @PostMapping("/{comicId}/view")
    public ResponseEntity<?> viewComic(@PathVariable String comicId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

        ComicResponse comicResponse = comicService.findById(new ObjectId(comicId));
        if (comicResponse == null) {
            throw new RecordNotFoundException("Comic not found with id: " + comicId);
        }

        comicService.incrementViews(new ObjectId(comicId));

        return ResponseEntity.ok("Comic viewed successfully");
    }

    //temporary
    @GetMapping("/admin")
    public ResponseEntity<List<Comic>> getAllComicsForAdmin() {
        List<Comic> comics = comicRepository.findAll();
        return ResponseEntity.ok(comics);
    }

    @PutMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<SuccessResponse> updateComicStatus(@PathVariable String id,
                                                             @RequestBody Map<String, String> request,
                                                             HttpServletRequest httpRequest) {
        String authorizationHeader = httpRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Không tìm thấy access token");
        }

        String accessToken = authorizationHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new BadCredentialsException("Access token đã hết hạn");
        }

        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
        if (user == null) {
            throw new RecordNotFoundException("Không tìm thấy người dùng");
        }

        String status = request.get("status");

        Optional<Comic> comicOpt = comicRepository.findById(new ObjectId(id));
        if (!comicOpt.isPresent()) {
            throw new RecordNotFoundException("Không tìm thấy truyện với ID: " + id);
        }

        Comic comic = comicOpt.get();
        comic.setStatus(status);
        comicRepository.save(comic);

        SuccessResponse response = new SuccessResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Khóa truyện thành công");
        response.setSuccess(true);
        return new ResponseEntity<SuccessResponse>(response, HttpStatus.OK);
    }
}