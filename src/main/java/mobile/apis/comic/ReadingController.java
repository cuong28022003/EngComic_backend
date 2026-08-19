package mobile.apis.comic;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.ReadingResponseDto;
import mobile.businesses.boundaries.comic.GetUserReadings;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reading")
@RequiredArgsConstructor
public class ReadingController {

    private final GetUserReadings getUserReadings;

    @GetMapping("")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<Page<ReadingResponseDto>> getMyReadings(
            @CurrentUserId String currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetUserReadings.Request request = GetUserReadings.Request.builder()
                .userId(currentUserId)
                .pageable(PageRequest.of(page, size))
                .build();

        GetUserReadings.Response response = getUserReadings.execute(request);
        return ResponseEntity.ok(response.getReadings());
    }
}

