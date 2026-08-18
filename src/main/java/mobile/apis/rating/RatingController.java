package mobile.apis.rating;

import lombok.RequiredArgsConstructor;
import mobile.apis.rating.dtos.RatingResponseDto;
import mobile.businesses.boundaries.rating.GetComicRatings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final GetComicRatings getComicRatings;

    @GetMapping("/comic/{comicId}")
    public ResponseEntity<Page<RatingResponseDto>> getRatingsByComic(
            @PathVariable String comicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetComicRatings.Request request = GetComicRatings.Request.builder()
                .comicId(comicId)
                .pageable(PageRequest.of(page, size))
                .build();

        GetComicRatings.Response response = getComicRatings.execute(request);
        return ResponseEntity.ok(response.getRatings());
    }
}
