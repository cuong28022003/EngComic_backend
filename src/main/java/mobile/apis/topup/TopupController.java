package mobile.apis.topup;

import lombok.RequiredArgsConstructor;
import mobile.apis.topup.dtos.TopupResponseDto;
import mobile.businesses.boundaries.topup.GetUserTopups;
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
@RequestMapping("/api/topup")
@RequiredArgsConstructor
public class TopupController {

    private final GetUserTopups getUserTopups;

    @GetMapping("/history")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<Page<TopupResponseDto>> getMyTopupHistory(
            @CurrentUserId String currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetUserTopups.Request request = GetUserTopups.Request.builder()
                .userId(currentUserId)
                .pageable(PageRequest.of(page, size))
                .build();

        GetUserTopups.Response response = getUserTopups.execute(request);
        return ResponseEntity.ok(response.getTopups());
    }
}
