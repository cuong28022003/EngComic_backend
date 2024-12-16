package mobile.controller;

import mobile.Service.*;
import mobile.mapping.ChapterMapping;
import mobile.mapping.NovelMapping;
import mobile.mapping.ReadingMapping;
import mobile.model.Entity.*;
import mobile.model.payload.request.chapter.DeleteChapterRequest;
import mobile.model.payload.request.chapter.UpdateChapterRequest;
import mobile.model.payload.request.novel.CreateNovelRequest;
import mobile.model.payload.request.novel.UpdateNovelRequest;
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
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mongodb.DBRef;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/novels")
@RequiredArgsConstructor
public class NovelResource {
    private static final Logger LOGGER = LogManager.getLogger(NovelResource.class);

    private final UserService userService;
    private final ComicService comicService;
    private final ChapterService chapterService;
    private final ReadingService readingService;
    private final CommentService commentService;
    private final SavedService savedService;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<List<NovelResponse>> getNovels(@RequestParam(defaultValue = "None") String status,
            @RequestParam(defaultValue = "tentruyen") String sort, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        List<Comic> comicList = null;
        List<NovelResponse> novelResponseList = new ArrayList<>();
        if (status.equals("None"))
            comicList = comicService.getComics(pageable);
        else
            comicList = comicService.findAllByStatus(status, pageable);

        if (comicList == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện");
        }
        for (Comic comic : comicList) {
            novelResponseList.add(NovelMapping.EntityToNovelResponse(comic));
        }
        return new ResponseEntity<List<NovelResponse>>(novelResponseList, HttpStatus.OK);
    }

//    @GetMapping("/theloai/{theloai}")
//    @ResponseBody
//    public ResponseEntity<List<Comic>> getNovelsByType(@PathVariable String theloai,
//            @RequestParam(defaultValue = "tentruyen") String sort, @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "3") int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
//        List<Comic> comicList = null;
//        // List<NovelResponse> novelResponseList=new ArrayList<>();
//        comicList = comicService.SearchByGenre(theloai, pageable);
//
//        if (comicList == null) {
//            throw new RecordNotFoundException("Không tìm thấy truyện");
//        }
//        // for (Novel novel: novelList
//        // ) {
//        // novelResponseList.add(NovelMapping.EntityToNovelResponse(novel));
//        // }
//        return new ResponseEntity<List<Comic>>(comicList, HttpStatus.OK);
//    }

    /*
     * @GetMapping("/search")
     * 
     * @ResponseBody
     * public ResponseEntity<List<Novel>> searchNovel(@RequestParam(defaultValue =
     * "") String theloai,
     * 
     * @RequestParam(defaultValue = "") String value, @RequestParam(defaultValue =
     * "tentruyen") String sort,
     * 
     * @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3")
     * int size) {
     * Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
     * List<Novel> novelList = null;
     * if (theloai.equals("")) {
     * novelList = novelService.SearchByTentruyen(value, pageable);
     * } else {
     * novelList = novelService.SearchByTypeAndTentruyen(theloai, value, pageable);
     * }
     * 
     * if (novelList == null) {
     * throw new RecordNotFoundException("Không tìm thấy truyện");
     * }
     * return new ResponseEntity<List<Novel>>(novelList, HttpStatus.OK);
     * }
     */
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<Comic>> searchNovelByFilters(
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
        List<Comic> comicList;
        // Kiểm tra nếu không có thể loại thì chỉ lọc theo tên
        if (!name.isEmpty()) {
            comicList = comicService.SearchByName(name, pageable);
        } else if (!genre.isEmpty()) {
            comicList = comicService.SearchByGenre(genre, pageable);
        } else {
            comicList = comicService.SearchByArtist(artist, pageable);
        }

        if (comicList == null || comicList.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy truyện phù hợp với tiêu chí.");
        }

        return new ResponseEntity<>(comicList, HttpStatus.OK);
    }

    @GetMapping("/novel/{url}")
    @ResponseBody
    public ResponseEntity<NovelDetailResponse> getNovelByName(@PathVariable String url) {

        Comic comic = comicService.findByUrl(url);
        int sochap = chapterService.countByDauTruyen(comic.getId());
        NovelDetailResponse novelDetailResponse = NovelMapping.EntityToNovelDetailResponse(comic, sochap);
        if (novelDetailResponse == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện");
        }
        return new ResponseEntity<NovelDetailResponse>(novelDetailResponse, HttpStatus.OK);
    }

    @GetMapping("/novel/{url}/chuong")
    @ResponseBody
    public ResponseEntity<List<Chapter>> getChapterpagination(@PathVariable String url,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("chapnumber"));

        Comic comic = comicService.findByUrl(url);
        if (comic == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện: " + url);
        }

        List<Chapter> chapterList = chapterService.findByDauTruyen(comic.getId(), pageable);
        if (chapterList == null) {
            throw new RecordNotFoundException("Không có chương nào được đăng");
        }
        return new ResponseEntity<List<Chapter>>(chapterList, HttpStatus.OK);
    }

    @GetMapping("/novel/{url}/mucluc")
    @ResponseBody
    public ResponseEntity<List<Object>> getMuclucpagination(@PathVariable String url,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("chapnumber"));

        Comic comic = comicService.findByUrl(url);
        if (comic == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện: " + url);
        }

        List<Object> chapterList = chapterService.getNameAndChapnumber(comic.getId(), pageable);
        if (chapterList == null) {
            throw new RecordNotFoundException("Không có chương nào được đăng");
        }
        return new ResponseEntity<List<Object>>(chapterList, HttpStatus.OK);
    }

    @GetMapping("/novel/{url}/mucluc/total")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getTotalChapter(@PathVariable String url) {

        Comic comic = comicService.findByUrl(url);
        if (comic == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện: " + url);
        }

        int chaptolal = chapterService.countByDauTruyen(comic.getId());
        Map<String, Integer> map = new HashMap<>();
        map.put("total", chaptolal);

        return new ResponseEntity<Map<String, Integer>>(map, HttpStatus.OK);
    }

    @GetMapping("/novel/{url}/chuong/{chapterNumber}")
    @ResponseBody
    public ResponseEntity<?> getChapter(
            @PathVariable String url,
            @PathVariable int chapterNumber,
            HttpServletRequest request) {
        // Tìm kiếm truyện theo URL
        Comic comic = comicService.findByUrl(url);
        if (comic == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện với URL: " + url);
        }

        // Tìm chapter theo số chương và đầu truyện
        Chapter chapter = chapterService.findByDauTruyenAndChapterNumber(comic.getId(), chapterNumber);
        if (chapter == null) {
            throw new RecordNotFoundException("Không tìm thấy chương " + chapterNumber);
        }

        // Kiểm tra access token trong header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            // Xác thực token và cập nhật trạng thái đọc
            if (!jwtUtils.validateExpiredToken(accessToken)) {
                String username = jwtUtils.getUserNameFromJwtToken(accessToken);
                User user = userService.findByUsername(username);
                if (user != null) {
                    readingService.upsertReading(new Reading(user, chapterNumber, comic));
                }
            }
        }

        // Phản hồi trực tiếp dữ liệu Chapter
        Map<String, Object> response = new HashMap<>();
        response.put("_id", chapter.getId());
        response.put("chapnumber", chapter.getChapnumber());
        response.put("dautruyenId", new DBRef("novels", comic.getId())); // Giả lập DBRef
        response.put("tenchap", chapter.getTenchap());
        response.put("danhSachAnh", chapter.getDanhSachAnh()); // Danh sách ảnh
        response.put("createAt", chapter.getCreateAt());
        response.put("updateAt", chapter.getUpdateAt());
        response.put("_class", chapter.getClass().getName());

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/tacgia/{tacgia}")
//    @ResponseBody
//    public ResponseEntity<List<Comic>> searchNovelByTacgia(@PathVariable String tacgia,
//            @RequestParam(defaultValue = "tentruyen") String sort, @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "3") int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
//        List<Comic> comicList = comicService.SearchByArtist(tacgia, pageable);
//
//        if (comicList == null) {
//            throw new RecordNotFoundException("Không tìm thấy truyện");
//        }
//        return new ResponseEntity<List<Comic>>(comicList, HttpStatus.OK);
//    }

    @GetMapping("/created") // lấy danh sách truyện được tạo theo username
    @ResponseBody
    public ResponseEntity<List<Comic>> getNovelsByUsername(@RequestParam(defaultValue = "None") String status,
            @RequestParam(defaultValue = "tentruyen") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam String username,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        User user = userService.findByUsername(username);
        System.out.println(user.getId().toHexString());
        List<Comic> comicList = comicService.SearchByUploader(user.getId(), pageable);
        if (comicList == null) {
            throw new RecordNotFoundException("Không tìm thấy truyện nào được đăng");
        }
        return new ResponseEntity<List<Comic>>(comicList, HttpStatus.OK);
    }

    @GetMapping("/readings") // lấy danh sách truyện mà người dùng đã đọc tạo theo username
    @ResponseBody
    public ResponseEntity<List<ReadingResponse>> getReadingsByUsername(
            @RequestParam(defaultValue = "None") String status,
            @RequestParam(defaultValue = "tentruyen") String sort,
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

            List<Reading> readingList = readingService.getReadings(user);
            if (readingList == null) {
                throw new RecordNotFoundException("Người dùng chưa đọc truyện nào");
            }
            List<ReadingResponse> readingResponseList = new ArrayList<>();
            for (Reading reading : readingList) {
                try {
                    int sochap = chapterService.countByDauTruyen(reading.getNovel().getId());
                    readingResponseList.add(ReadingMapping.EntityToResponese(reading, sochap));
                } catch (Exception ex) {

                }
            }

            return new ResponseEntity<List<ReadingResponse>>(readingResponseList, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

    @GetMapping("/readingsdefault") // lấy danh sách truyện được tạo theo username
    @ResponseBody
    public ResponseEntity<List<ReadingResponse>> getReadingsDefault(@RequestParam(defaultValue = "None") String status,
            @RequestParam(defaultValue = "tentruyen") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        List<Comic> comicList = comicService.getComics(pageable);
        if (comicList == null) {
            throw new RecordNotFoundException("Không có tìm thấy truyện");
        }
        List<ReadingResponse> readingResponseList = new ArrayList<>();
        for (Comic comic : comicList) {
            try {
                int sochap = chapterService.countByDauTruyen(comic.getId());
                readingResponseList.add(ReadingMapping.NovelToResponese(comic, sochap));
            } catch (Exception ex) {

            }

        }
        return new ResponseEntity<List<ReadingResponse>>(readingResponseList, HttpStatus.OK);

    }

    @GetMapping("/reading") // API lấy thông tin đọc của người dùng theo URL
    @ResponseBody
    public ResponseEntity<ReadingResponse> getReadingByUrl(
            @RequestParam("url") String url,
            HttpServletRequest request) {
        // Lấy thông tin Access Token từ Header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            // Kiểm tra xem token có hết hạn không
            if (jwtUtils.validateExpiredToken(accessToken)) {
                throw new BadCredentialsException("Access token đã hết hạn");
            }

            // Lấy thông tin người dùng từ token
            String username = jwtUtils.getUserNameFromJwtToken(accessToken);
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new RecordNotFoundException("Không tìm thấy người dùng");
            }

            Comic comic = comicService.findByUrl(url);
            if (comic == null) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }

            // Tìm hoặc tạo mới Reading
            Reading reading = readingService.getReading(user, comic).orElseGet(() -> {
                Reading newReading = new Reading();
                newReading.setUser(user);
                newReading.setNovel(comic);
                newReading.setChapnumber(1); // Giá trị mặc định
                readingService.save(newReading);
                return newReading;
            });
            // Tính số chương của truyện
            int sochap = chapterService.countByDauTruyen(reading.getNovel().getId());

            // Chuyển đổi entity sang response DTO
            ReadingResponse readingResponse = ReadingMapping.EntityToResponese(reading, sochap);

            // Trả về kết quả
            return new ResponseEntity<>(readingResponse, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy Access Token");
        }
    }


    @PostMapping("novel/create") //Tạo đầu truyện
    @ResponseBody
    public ResponseEntity<SuccessResponse> createNovel(@RequestBody CreateNovelRequest createNovelRequest,
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

            Comic newComic = NovelMapping.CreateRequestToNovel(createNovelRequest);
            newComic.setUploader(user);
            comicService.SaveComic(newComic);

            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Đăng truyện mới thành công");
            response.setSuccess(true);
            return new ResponseEntity<SuccessResponse>(response, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Không tìm thấy access token");
        }
    }

    @PutMapping("novel/edit") // Update đầu truyện
    @ResponseBody
    public ResponseEntity<SuccessResponse> editNovel(@RequestBody UpdateNovelRequest updateNovelRequest,
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

            ObjectId truyenId = new ObjectId(updateNovelRequest.getId());
            Optional<Comic> findNovel = comicService.findById(truyenId);
            if (!findNovel.isPresent()) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }

            Comic oldComic = findNovel.get();
            if (oldComic.getUploader().getUsername().equals(user.getUsername())) {
                NovelMapping.UpdateRequestToNovel(updateNovelRequest, oldComic);
                comicService.SaveComic(oldComic);
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

    @DeleteMapping("/novel/{url}") // Delete đầu truyện, sẽ delete chapter, comment, reading liên kết cùng
    @ResponseBody
    public ResponseEntity<SuccessResponse> deleteNovel(@PathVariable String url, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token đã hết hạn");
            }

            User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));

            if (user == null)
                throw new RecordNotFoundException("Không tìm thấy người dùng");

            Comic findComic = comicService.findByUrl(url);
            if (findComic == null) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }

            if (findComic.getUploader().getUsername().equals(user.getUsername())) {
                commentService.DeleteCommentByComicUrl(findComic.getUrl());
                readingService.deleteAllReadingByComic(findComic);
                chapterService.DeleteAllChapterByComic(findComic);
                savedService.DeleteSavedByComic(findComic);
                comicService.DeleteComic(findComic);
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

    @GetMapping("/novel/newupdate")
    @ResponseBody
    public ResponseEntity<List<ChapterNewUpdateResponse>> getNewestUpdate(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Chapter> chapters = chapterService.getChaptersNewUpdate(pageable);
        if (chapters == null) {
            throw new RecordNotFoundException("Truyện không có chương nào !!!");
        }
        List<ChapterNewUpdateResponse> list = ChapterMapping.getListChapterNewUpdateResponse(chapters);
        return new ResponseEntity<List<ChapterNewUpdateResponse>>(list, HttpStatus.OK);
    }

    @PostMapping("/novel/chuong/create")
    @ResponseBody
    public ResponseEntity<SuccessResponse> CreateChapter(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("url") String url,
            @RequestParam("tenchap") String tenchap,
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
            List<String> danhSachAnh = new ArrayList<>();
            try {
                for (MultipartFile file : files) {
                    Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    String imageUrl = (String) uploadResult.get("secure_url");
                    danhSachAnh.add(imageUrl);
                }
            } catch (IOException e) {
                SuccessResponse errorResponse = new SuccessResponse();
                errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                errorResponse.setMessage("Upload failed: " + e.getMessage());
                errorResponse.setSuccess(false);
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Tạo chương mới
            int chapnumber = chapterService.countByDauTruyen(comic.getId()) + 1;
            String tenChuongDayDu = "Chương " + chapnumber + ": " + tenchap;

            Chapter newChapter = new Chapter();
            newChapter.setDautruyenId(comic);
            newChapter.setDanhSachAnh(danhSachAnh);
            newChapter.setChapnumber(chapnumber);
            newChapter.setTenchap(tenChuongDayDu);

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

    @PutMapping("/novel/chuong/edit")
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
            Chapter chapter = chapterService.findByDauTruyenAndChapterNumber(comic.getId(),
                    updateChapterRequest.getChapnumber());
            if (chapter == null) {
                throw new RecordNotFoundException("Không tìm thấy chương cần chỉnh sửa");
            }
            if (comic.getUploader().getUsername().equals(user.getUsername())) {
                String tenchap = updateChapterRequest.getTenchap();
                chapter.setTenchap(tenchap);
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

    @DeleteMapping("/novel/chuong")
    @ResponseBody
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
            Chapter chapter = chapterService.findByDauTruyenAndChapterNumber(comic.getId(),
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

    @PatchMapping("/increment-views/{url}")
    public ResponseEntity<Comic> incrementViews(@PathVariable String url) {
        try {
            Comic updatedComic = comicService.incrementViews(url);
            return ResponseEntity.ok(updatedComic);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
