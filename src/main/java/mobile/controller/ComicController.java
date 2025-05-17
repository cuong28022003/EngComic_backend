package mobile.controller;

import mobile.Service.*;
import mobile.mapping.ChapterMapping;
import mobile.mapping.ComicMapping;
import mobile.mapping.ReadingMapping;
import mobile.model.Entity.*;
import mobile.model.payload.request.novel.CreateComicRequest;
import mobile.model.payload.request.novel.UpdateComicRequest;
import mobile.model.payload.response.*;
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
//    private final CommentService commentService;
    private final SavedService savedService;
    private final RatingService ratingService;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Page<ComicResponse>> getComics(@RequestParam(defaultValue = "None") String status,
                                                         @RequestParam(defaultValue = "name") String sort, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "desc") String order,
                                                         @RequestParam(defaultValue = "3") int size) {

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<Comic> comicPage = comicService.getComics(pageable);
        Page<ComicResponse> responsePage = comicPage.map(comic -> ComicMapping.EntityToComicResponse(comic));
        return new ResponseEntity<Page<ComicResponse>>(responsePage, HttpStatus.OK);
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Page<ComicResponse>> searchComic(
            @RequestParam(defaultValue = "") String artist,
            @RequestParam(defaultValue = "") String genre,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        // Thiết lập thứ tự sắp xếp (ASC/DESC)
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<Comic> comicPage;
        // Kiểm tra nếu không có thể loại thì chỉ lọc theo tên
        if (!name.isEmpty()) {
            comicPage = comicService.findByName(name, pageable);
        } else if (!genre.isEmpty()) {
            comicPage = comicService.findByGenre(genre, pageable);
        } else {
            comicPage = comicService.findByArtist(artist, pageable);
        }

        if (comicPage == null || comicPage.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy truyện phù hợp với tiêu chí.");
        }

        Page<ComicResponse> responsePage = comicPage.map(comic -> ComicMapping.EntityToComicResponse(comic));

        return new ResponseEntity<Page<ComicResponse>>(responsePage, HttpStatus.OK);
    }

    @GetMapping("/{url}")
    @ResponseBody
    public ResponseEntity<ComicDetailResponse> getComicByUrl(@PathVariable String url) {

        Comic comic = comicService.findByUrl(url);
        int chapterCount = chapterService.countChaptersByComic(comic);

        List<Rating> ratings = ratingService.getRatingsByComicId(comic.getId());
        double averageRating = ratings.stream().mapToInt(Rating::getRating).average().orElse(0.0);
        int totalRatings = ratings.size();
        ComicDetailResponse comicDetailResponse = ComicMapping.EntityToComicDetailResponse(comic, chapterCount, averageRating, totalRatings);
        if (comicDetailResponse == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện");
        }
        return new ResponseEntity<ComicDetailResponse>(comicDetailResponse, HttpStatus.OK);
    }

    @PostMapping("")
    @ResponseBody
    public ResponseEntity<SuccessResponse> createComic(@RequestBody CreateComicRequest createComicRequest,
                                                       HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token đã hết hạn");
            }

            User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

            if (user == null)
                throw new RecordNotFoundException("Không tìm thấy người dùng");

            Comic newComic = ComicMapping.CreateRequestToComic(createComicRequest);
            newComic.setUploader(user);
            comicService.saveComic(newComic);

            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Đăng truyện mới thành công");
            response.setSuccess(true);
            return new ResponseEntity<SuccessResponse>(response, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

    @PutMapping("/{url}")
    @ResponseBody
    public ResponseEntity<SuccessResponse> updateComic(@RequestBody UpdateComicRequest updateComicRequest,
                                                       HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token đã hết hạn");
            }

            User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

            if (user == null)
                throw new RecordNotFoundException("Không tìm thấy người dùng");

            ObjectId comicId = new ObjectId(updateComicRequest.getId());
            Optional<Comic> comic = comicService.findById(comicId);
            if (!comic.isPresent()) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }

            Comic oldComic = comic.get();
            if (oldComic.getUploader().getUsername().equals(user.getUsername())) {
                ComicMapping.UpdateRequestToComic(updateComicRequest, oldComic);
                comicService.saveComic(oldComic);
            } else {
                throw new BadCredentialsException("Không thể chỉnh sửa truyện của người khác");
            }

            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Cập nhật truyện thành công");
            response.setSuccess(true);
            return new ResponseEntity<SuccessResponse>(response, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

    @DeleteMapping("/{url}")
    public ResponseEntity<SuccessResponse> deleteComic(@PathVariable String url, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token đã hết hạn");
            }

            User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

            if (user == null)
                throw new RecordNotFoundException("Không tìm thấy người dùng");

            Comic comic = comicService.findByUrl(url);
            if (comic == null) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }

            if (comic.getUploader().getUsername().equals(user.getUsername())) {
//                commentService.DeleteCommentByComicUrl(comic.getUrl());
                readingService.deleteAllReadingByComic(comic);
                chapterService.DeleteAllChapterByComic(comic);
                savedService.DeleteSavedByComic(comic);
                comicService.DeleteComic(comic);
            } else {
                throw new BadCredentialsException("Không thể chỉnh sửa truyện của người khác");
            }

            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Xóa truyện thành công");
            response.setSuccess(true);
            return new ResponseEntity<SuccessResponse>(response, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không có access token");
        }
    }


    @GetMapping("/uploader/{userId}")
    public ResponseEntity<Page<Comic>> getComicsByUploaderId(@RequestParam(defaultValue = "None") String status,
                                                           @RequestParam(defaultValue = "name") String sort,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size,
                                                           @PathVariable String userId,
                                                           HttpServletRequest request) {
        User user = userService.findById(new ObjectId(userId));
        System.out.println(user.getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<Comic> comicList = comicService.findByUploader(user, pageable);
        if (comicList == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện nào được đăng");
        }
        return new ResponseEntity<Page<Comic>>(comicList, HttpStatus.OK);
    }



    @PatchMapping("/{url}/increment-views")
    public ResponseEntity<Comic> incrementViews(@PathVariable String url) {
        try {
            Comic updatedComic = comicService.incrementViews(url);
            return ResponseEntity.ok(updatedComic);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
