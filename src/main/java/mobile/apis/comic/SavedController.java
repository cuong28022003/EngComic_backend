package mobile.apis.comic;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.SaveComicRequest;
import mobile.apis.comic.dtos.SavedResponseDto;
import mobile.businesses.boundaries.comic.CheckSavedComic;
import mobile.businesses.boundaries.comic.GetUserSavedComics;
import mobile.businesses.boundaries.comic.SaveComic;
import mobile.businesses.boundaries.comic.UnsaveComic;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saved")
@RequiredArgsConstructor
public class SavedController {

    private final GetUserSavedComics getUserSavedComics;
    private final SaveComic saveComic;
    private final UnsaveComic unsaveComic;
    private final CheckSavedComic checkSavedComic;

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

    @GetMapping("/user/{userId}")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<Page<SavedResponseDto>> getUserSavedComics(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetUserSavedComics.Request request = GetUserSavedComics.Request.builder()
                .userId(userId)
                .pageable(PageRequest.of(page, size))
                .build();

        GetUserSavedComics.Response response = getUserSavedComics.execute(request);
        return ResponseEntity.ok(response.getSavedComics());
    }

    @GetMapping("/check")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<SavedResponseDto> checkSavedComic(
            @RequestParam String userId,
            @RequestParam String comicId) {

        CheckSavedComic.Request request = CheckSavedComic.Request.builder()
                .userId(userId)
                .comicId(comicId)
                .build();

        CheckSavedComic.Response response = checkSavedComic.execute(request);
        return ResponseEntity.ok(response.getSaved());
    }

    @PostMapping("")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<SavedResponseDto> saveComic(
            @CurrentUserId String currentUserId,
            @RequestBody SaveComicRequest body) {

        String targetUserId = (currentUserId != null && !currentUserId.isBlank())
                ? currentUserId
                : body.getUserId();

        SaveComic.Request request = SaveComic.Request.builder()
                .userId(targetUserId)
                .comicId(body.getComicId())
                .build();

        SaveComic.Response response = saveComic.execute(request);
        return ResponseEntity.ok(response.getSaved());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<Void> unsaveComic(@PathVariable String id) {
        unsaveComic.execute(UnsaveComic.Request.builder().id(id).build());
        return ResponseEntity.noContent().build();
    }
}
