package mobile.apis.comic;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.businesses.boundaries.comic.GetComicDetail;
import mobile.businesses.boundaries.comic.GetComics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comics")
@RequiredArgsConstructor
public class ComicController {

    private final GetComics getComics;
    private final GetComicDetail getComicDetail;

    @GetMapping("")
    public ResponseEntity<Page<ComicResponseDto>> getComics(
            @RequestParam(defaultValue = "None") String status,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "10") int size) {

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        GetComics.Request request = GetComics.Request.builder()
                .status(status)
                .pageable(pageable)
                .build();

        GetComics.Response response = getComics.execute(request);
        return ResponseEntity.ok(response.getComics());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComicResponseDto> getComicById(@PathVariable String id) {
        GetComicDetail.Request request = GetComicDetail.Request.builder()
                .id(id)
                .build();

        GetComicDetail.Response response = getComicDetail.execute(request);
        return ResponseEntity.ok(response.getComic());
    }
}
