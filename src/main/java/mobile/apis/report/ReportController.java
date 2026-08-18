package mobile.apis.report;

import lombok.RequiredArgsConstructor;
import mobile.apis.report.dtos.ReportResponseDto;
import mobile.businesses.boundaries.report.GetReports;
import mobile.security.constants.AppAuthorities;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final GetReports getReports;

    @GetMapping("")
    @PreAuthorize(AppAuthorities.HAS_ROLE_ADMIN)
    public ResponseEntity<Page<ReportResponseDto>> getReports(
            @RequestParam(defaultValue = "None") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetReports.Request request = GetReports.Request.builder()
                .status(status)
                .pageable(PageRequest.of(page, size))
                .build();

        GetReports.Response response = getReports.execute(request);
        return ResponseEntity.ok(response.getReports());
    }
}
