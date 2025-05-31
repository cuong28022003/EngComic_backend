package mobile.controller;

import mobile.Service.*;
import mobile.mapping.ReportMapping;
import mobile.model.Entity.Report;
import mobile.model.payload.request.report.ReportRequest;
import mobile.model.payload.response.ReportResponse;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    UserService userService;

    @Autowired
    ComicService comicService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ReportService reportService;

    @Autowired
    ReportMapping reportMapping; // Inject ReportMapping instance

    @GetMapping("/all")
    public ResponseEntity<Page<ReportResponse>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        // Validate token
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new BadCredentialsException("Token expired");
        }

        Page<Report> reports = reportService.getAllReports(PageRequest.of(page, size));
        return ResponseEntity.ok(reports.map(reportMapping::entityToResponse));
    }

    @GetMapping("/comic/{comicId}")
    public ResponseEntity<Page<ReportResponse>> getReportsForComic(
            @PathVariable String comicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Report> reports = reportService.getReportsForComic(new ObjectId(comicId), PageRequest.of(page, size));
        return ResponseEntity.ok(reports.map(reportMapping::entityToResponse));
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestBody ReportRequest reportRequest,
            HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new BadCredentialsException("Token expired");
        }

        Report report = reportMapping.requestToEntity(reportRequest);
        return ResponseEntity.ok(reportMapping.entityToResponse(reportService.createOrUpdateReport(report)));
    }

    @PutMapping("/{reportId}/status")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable String reportId,
            @RequestParam String status,
            HttpServletRequest request) {

        // Validate token
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new BadCredentialsException("Token expired");
        }

        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }

        Report updatedReport = reportService.updateReportStatus(new ObjectId(reportId), status);
        return ResponseEntity.ok(reportMapping.entityToResponse(updatedReport));
    }

    @GetMapping("/summary/{comicId}")
    public Map<String, Object> getReportSummary(@PathVariable String comicId) {
        List<Report> reports = reportService.getReportsByComicId(new ObjectId(comicId));
        int total = reports.size();
        long pending = reports.stream().filter(r -> "PENDING".equals(r.getStatus())).count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalReports", total);
        result.put("pendingReports", pending);
        return result;
    }

    @GetMapping("/status")
    public ResponseEntity<Page<ReportResponse>> getReportsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }

        Page<Report> reports = reportService.getReportsByStatus(status, PageRequest.of(page, size));
        return ResponseEntity.ok(reports.map(reportMapping::entityToResponse));
    }

    // Helper method to validate status
    private boolean isValidStatus(String status) {
        return status != null && (status.equals("PENDING") ||
                status.equals("APPROVED") ||
                status.equals("REJECTED") ||
                status.equals("RESOLVED"));
    }
}