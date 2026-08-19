package mobile.apis.comic;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.CommentResponseDto;
import mobile.businesses.boundaries.comic.GetComments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final GetComments getComments;

    @GetMapping("")
    public ResponseEntity<Page<CommentResponseDto>> getComments(
            @RequestParam String comicUrl,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetComments.Request request = GetComments.Request.builder()
                .comicUrl(comicUrl)
                .pageable(PageRequest.of(page, size))
                .build();

        GetComments.Response response = getComments.execute(request);
        return ResponseEntity.ok(response.getComments());
    }
}

