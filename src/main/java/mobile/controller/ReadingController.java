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
import mobile.model.payload.response.ReadingResponse;
import mobile.security.JWT.JwtUtils;
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

    @GetMapping("")
    @ResponseBody
    public ResponseEntity<Page<ReadingResponse>> getReadingsByUser(
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

            Page<Reading> readingPage = readingService.getReadings(user, pageable);
            if (readingPage == null) {
                throw new RecordNotFoundException("Người dùng chưa đọc truyện nào");
            }
            Page<ReadingResponse> readingResponsePage = readingPage.map(reading -> {
                int chapterCount = chapterService.countChaptersByComic(reading.getComic());
                return ReadingMapping.EntityToResponese(reading, chapterCount);
            });

            return new ResponseEntity<Page<ReadingResponse>>(readingResponsePage, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

    @GetMapping("/{url}")
    @ResponseBody
    public ResponseEntity<ReadingResponse> getReadingByUrl(@PathVariable String url, HttpServletRequest request) {
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

            Comic comic = comicService.findByUrl(url);

            Reading reading = readingService.findByComicAndUser(comic, user);
            if (reading == null) {
                reading = new Reading();
                                reading.setUser(user);
                                reading.setComic(comic);
                                reading.setChapterNumber(1);
                                readingService.saveReading(reading);
            }

            int chapterCount = chapterService.countChaptersByComic(reading.getComic());
            ReadingResponse readingResponse = ReadingMapping.EntityToResponese(reading, chapterCount);

            return new ResponseEntity<ReadingResponse>(readingResponse, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

   @PostMapping("")
   @ResponseBody
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

           Comic comic = comicService.findByUrl(readingRequest.getUrl());

           Reading reading = new Reading();
           reading.setUser(user);
           reading.setComic(comic);
           reading.setChapterNumber(readingRequest.getChapterNumber());
           readingService.saveReading(reading);

           int chapterCount = chapterService.countChaptersByComic(reading.getComic());
           ReadingResponse readingResponse = ReadingMapping.EntityToResponese(reading, chapterCount);

           return new ResponseEntity<ReadingResponse>(readingResponse, HttpStatus.CREATED);
       } else {
           throw new BadCredentialsException("Không tìm thấy access token");
       }
   }
}
