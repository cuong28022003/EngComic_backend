package mobile.apis.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.CheckInResponseDto;
import mobile.businesses.boundaries.user.CheckIn;
import mobile.businesses.boundaries.user.GetCheckInStatus;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check-in")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckIn checkIn;
    private final GetCheckInStatus getCheckInStatus;

    @PostMapping("")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<CheckInResponseDto> checkIn(@CurrentUserId String currentUserId) {
        CheckIn.Response response = checkIn.execute(CheckIn.Request.builder()
                .userId(currentUserId)
                .build());
        return ResponseEntity.ok(response.getResult());
    }

    @GetMapping("/today")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<CheckInResponseDto> getToday(@CurrentUserId String currentUserId) {
        GetCheckInStatus.Response response = getCheckInStatus.execute(GetCheckInStatus.Request.builder()
                .userId(currentUserId)
                .build());
        return ResponseEntity.ok(response.getResult());
    }

    @GetMapping("/month")
    @PreAuthorize(AppAuthorities.IS_AUTHENTICATED)
    public ResponseEntity<CheckInResponseDto> getMonth(
            @CurrentUserId String currentUserId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        GetCheckInStatus.Response response = getCheckInStatus.execute(GetCheckInStatus.Request.builder()
                .userId(currentUserId)
                .year(year)
                .month(month)
                .build());
        return ResponseEntity.ok(response.getResult());
    }
}
