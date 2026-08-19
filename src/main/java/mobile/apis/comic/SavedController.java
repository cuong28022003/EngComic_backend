package mobile.apis.comic;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.SavedResponseDto;
import mobile.businesses.boundaries.comic.GetUserSavedComics;
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
@RequestMapping("/api/saved")
@RequiredArgsConstructor
public class SavedController {

    private final GetUserSavedComics getUserSavedComics;

    @GetMapping("")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<Page<SavedResponseDto>> getMySavedComics(
            @CurrentUserId String currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetUserSavedComics.Request request = GetUserSavedComics.Request.builder()
                .userId(currentUserId)
                .pageable(PageRequest.of(page, size))
                .build();

        GetUserSavedComics.Response response = getUserSavedComics.execute(request);
        return ResponseEntity.ok(response.getSavedComics());
    }
}

