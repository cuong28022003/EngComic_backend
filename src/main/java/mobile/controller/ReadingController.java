package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Handler.RecordNotFoundException;
import mobile.Service.ChapterService;
import mobile.Service.ComicService;
import mobile.Service.ReadingService;
import mobile.Service.UserService;
import mobile.mapping.ReadingMapping;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Reading;
import mobile.model.Entity.User;
import mobile.model.payload.request.reading.ReadingRequest;
import mobile.model.payload.response.reading.ReadingResponse;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("api/reading")
@RequiredArgsConstructor
public class ReadingController {
    private final ReadingService readingService;
    private final UserService userService;
    private final ChapterService chapterService;
    private final ComicService comicService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReadingResponse>> getReadingsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "None") String status,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token đã hết hạn");
            }

            User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

            if (user == null)
                throw new RecordNotFoundException("Không tìm thấy người dùng");

            Page<ReadingResponse> readingPage = readingService.getReadingsByUserId(new ObjectId(userId), pageable);
            if (readingPage == null) {
                throw new RecordNotFoundException("Người dùng chưa đọc truyện nào");
            }

            return ResponseEntity.ok(readingPage);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

    @GetMapping("")
    public ResponseEntity<ReadingResponse> getReadingByUserIdAndComicId(@RequestParam String comicId,
                                                                        @RequestParam String userId,
                                                                        HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token đã hết hạn");
            }

            User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

            if (user == null) {
                throw new RecordNotFoundException("Không tìm thấy người dùng");
            }

            ReadingResponse reading = readingService.findByUserIdAndComicId(new ObjectId(userId), new ObjectId(comicId));

            return ResponseEntity.ok(reading);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

    @PostMapping("")
    public ResponseEntity<ReadingResponse> addReading(
            @RequestBody ReadingRequest readingRequest,
            HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token đã hết hạn");
            }

            User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

            if (user == null) {
                throw new RecordNotFoundException("Không tìm thấy người dùng");
            }

            ObjectId userId = new ObjectId(readingRequest.getUserId());
            ObjectId comicId = new ObjectId(readingRequest.getComicId());
            int chapterNumber = readingRequest.getChapterNumber();

            ReadingResponse reading = readingService.createReading(userId, comicId, chapterNumber);

            return ResponseEntity.ok(reading);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }
}
