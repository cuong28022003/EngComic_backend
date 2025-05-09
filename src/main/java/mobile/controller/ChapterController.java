package mobile.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import mobile.Handler.RecordNotFoundException;
import mobile.Service.ChapterService;
import mobile.Service.ComicService;
import mobile.Service.ReadingService;
import mobile.Service.UserService;
import mobile.mapping.ChapterMapping;
import mobile.model.Entity.Chapter;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Reading;
import mobile.model.Entity.User;
import mobile.model.payload.request.chapter.CreateChapterRequest;
import mobile.model.payload.request.chapter.DeleteChapterRequest;
import mobile.model.payload.request.chapter.UpdateChapterRequest;
import mobile.model.payload.response.ChapterResponse;
import mobile.model.payload.response.SuccessResponse;
import mobile.security.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("api/chapter")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;
    private final ComicService comicService;
    private final UserService userService;
    private final ReadingService readingService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("")
    public ResponseEntity<Page<Chapter>> getChaptersByComic(@RequestParam String url,
                                                            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {

        Comic comic = comicService.findByUrl(url);
        if (comic == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện: " + url);
        }

        Page<Chapter> chapterPage = chapterService.findByComic(comic, page, size);
        if (chapterPage == null) {
            throw new RecordNotFoundException("Không có chương nào được đăng");
        }
        return new ResponseEntity<Page<Chapter>>(chapterPage, HttpStatus.OK);
    }

    @GetMapping("/{url}/{chapterNumber}")
    @ResponseBody
    public ResponseEntity<ChapterResponse> getChapter(
            @PathVariable String url,
            @PathVariable String chapterNumber,
            HttpServletRequest request) {
        // Tìm kiếm truyện theo URL
        Comic comic = comicService.findByUrl(url);
        if (comic == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện với URL: " + url);
        }

        // Tìm chapter theo số chương và đầu truyện
        Chapter chapter = chapterService.findByComicAndChapterNumber(comic, Integer.parseInt(chapterNumber));
        if (chapter == null) {
            throw new RecordNotFoundException("Không tìm thấy chương " + chapterNumber);
        }

        int totalChapters = chapterService.countChaptersByComic(comic);

        // Kiểm tra access token trong header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
        }

        ChapterResponse chapterResponse = ChapterMapping.toChapterResponse(chapter, totalChapters);

        return ResponseEntity.ok(chapterResponse);
    }

    @PostMapping("")
                public ResponseEntity<SuccessResponse> CreateChapter(
                        @RequestParam("name") String name,
                        @RequestParam("url") String url,
                        @RequestParam("images") MultipartFile[] images,
                        @RequestParam(value = "cover", required = false) MultipartFile cover,
                        HttpServletRequest request) {

                    String authorizationHeader = request.getHeader(AUTHORIZATION);

                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String accessToken = authorizationHeader.substring("Bearer ".length());

                        // Kiểm tra token
                        if (jwtUtils.validateExpiredToken(accessToken)) {
                            throw new BadCredentialsException("access token đã hết hạn");
                        }

                        // Tìm thông tin người dùng từ token
                        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
                        if (user == null) {
                            throw new RecordNotFoundException("Không tìm thấy người dùng");
                        }

                        // Tìm truyện qua URL
                        Comic comic = comicService.findByUrl(url);
                        if (comic == null) {
                            throw new RecordNotFoundException("Không tìm thấy truyện");
                        }

                        // Kiểm tra quyền chỉnh sửa
                        if (!comic.getUploader().getUsername().equals(user.getUsername())) {
                            throw new BadCredentialsException("Không thể chỉnh sửa truyện của người khác");
                        }

                        // Upload từng ảnh lên Cloudinary
                        List<String> files = new ArrayList<>();
                        String coverUrl = null;
                        try {
                            for (MultipartFile file : images) {
                                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                                String imageUrl = (String) uploadResult.get("secure_url");
                                files.add(imageUrl);
                            }

                            // Upload cover nếu có, nếu không lấy ảnh đầu tiên làm cover
                            if (cover != null) {
                                Map coverUploadResult = cloudinary.uploader().upload(cover.getBytes(), ObjectUtils.emptyMap());
                                coverUrl = (String) coverUploadResult.get("secure_url");
                            } else if (!files.isEmpty()) {
                                coverUrl = files.get(0);
                            }
                        } catch (IOException e) {
                            SuccessResponse errorResponse = new SuccessResponse();
                            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                            errorResponse.setMessage("Upload failed: " + e.getMessage());
                            errorResponse.setSuccess(false);
                            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                        }

                        // Tạo chương mới
                        int chapterNumber = chapterService.countChaptersByComic(comic) + 1;
                        String fullTitle = "Chương " + chapterNumber + ": " + name;

                        Chapter newChapter = new Chapter();
                        newChapter.setComic(comic);
                        newChapter.setImages(files);
                        newChapter.setChapterNumber(chapterNumber);
                        newChapter.setName(fullTitle);
                        newChapter.setCover(coverUrl);

                        chapterService.SaveChapter(newChapter);

                        // Trả phản hồi thành công
                        SuccessResponse response = new SuccessResponse();
                        response.setStatus(HttpStatus.OK.value());
                        response.setMessage("Đăng chương mới thành công");
                        response.setSuccess(true);

                        return new ResponseEntity<>(response, HttpStatus.OK);

                    } else {
                        throw new BadCredentialsException("Không tìm thấy access token");
                    }
                }

    @PutMapping("/{chapterNumber}")
    @ResponseBody
    public ResponseEntity<SuccessResponse> UpdateChapter(@RequestBody UpdateChapterRequest updateChapterRequest,
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

            if (updateChapterRequest.getContent().length() < 10) {
                throw new BadCredentialsException("Nội dung phải dài hơn 10 ký tự");
            }
            Comic comic = comicService.findByUrl(updateChapterRequest.getUrl());
            if (comic == null) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }
            Chapter chapter = chapterService.findByComicAndChapterNumber(comic,
                    updateChapterRequest.getChapnumber());
            if (chapter == null) {
                throw new RecordNotFoundException("Không tìm thấy chương cần chỉnh sửa");
            }
            if (comic.getUploader().getUsername().equals(user.getUsername())) {
                String name = updateChapterRequest.getName();
                chapter.setName(name);
//                chapter.setContent(updateChapterRequest.getContent());
                chapterService.SaveChapter(chapter);
            } else {
                throw new BadCredentialsException("Không thể chỉnh sửa truyện của người khác");
            }

            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Cập nhật chương thành công");
            response.setSuccess(true);
            return new ResponseEntity<SuccessResponse>(response, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

    @DeleteMapping("")
    public ResponseEntity<SuccessResponse> DeleteChapter(@RequestBody DeleteChapterRequest deleteChapterRequest,
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

            Comic comic = comicService.findByUrl(deleteChapterRequest.getUrl());
            if (comic == null) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }
            Chapter chapter = chapterService.findByComicAndChapterNumber(comic,
                    deleteChapterRequest.getChapterNumber());
            if (chapter == null) {
                throw new RecordNotFoundException("Không tìm thấy chương cần chỉnh sửa");
            }

            if (comic.getUploader().getUsername().equals(user.getUsername())) {
                chapterService.DeleteChapter(chapter);
            } else {
                throw new BadCredentialsException("Không thể chỉnh sửa truyện của người khác");
            }

            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Xóa chương thành công");
            response.setSuccess(true);
            return new ResponseEntity<SuccessResponse>(response, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }


}
