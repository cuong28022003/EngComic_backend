package mobile.apis.chapter;

import lombok.RequiredArgsConstructor;
import mobile.apis.chapter.dtos.ChapterResponseDto;
import mobile.businesses.boundaries.chapter.GetChapterDetail;
import mobile.businesses.boundaries.chapter.GetChapters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chapter")
@RequiredArgsConstructor
public class ChapterController {

    private final GetChapters getChapters;
    private final GetChapterDetail getChapterDetail;

    @GetMapping("/comic/{comicId}")
    public ResponseEntity<Page<ChapterResponseDto>> getChaptersByComicId(
            @PathVariable String comicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetChapters.Request request = GetChapters.Request.builder()
                .comicId(comicId)
                .pageable(PageRequest.of(page, size))
                .build();

        GetChapters.Response response = getChapters.execute(request);
        return ResponseEntity.ok(response.getChapters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponseDto> getChapterById(@PathVariable String id) {
        GetChapterDetail.Request request = GetChapterDetail.Request.builder()
                .chapterId(id)
                .build();

        GetChapterDetail.Response response = getChapterDetail.execute(request);
        return ResponseEntity.ok(response.getChapter());
    }
}
