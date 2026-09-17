package mobile.apis.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.reader.dtos.*;
import mobile.businesses.boundaries.reader.*;
import mobile.security.constants.AppAuthorities;
import mobile.security.resolver.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/toeic/tests")
@RequiredArgsConstructor
public class ToeicTestController {

    private final CreateToeicTestBoundary createToeicTest;
    private final UpdateToeicTestBoundary updateToeicTest;
    private final DeleteToeicTestBoundary deleteToeicTest;
    private final GetToeicTestListBoundary getToeicTestList;
    private final GetToeicTestDetailBoundary getToeicTestDetail;
    private final SubmitToeicSessionBoundary submitToeicSession;
    private final GetToeicDashboardBoundary getToeicDashboard;
    private final ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<ToeicDashboardDto> getDashboard(@CurrentUserId String userId) {
        GetToeicDashboardBoundary.Response res = getToeicDashboard.execute(
                GetToeicDashboardBoundary.Request.builder().userId(userId).build());
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<Page<ToeicTestSummaryDto>> getTests(
            @CurrentUserId String userId,
            Pageable pageable) {
        GetToeicTestListBoundary.Response res = getToeicTestList.execute(
                GetToeicTestListBoundary.Request.builder()
                        .userId(userId)
                        .pageable(pageable)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_CARD_READ)
    public ResponseEntity<ToeicTestDetailDto> getTestDetail(
            @CurrentUserId String userId,
            @PathVariable String id) {
        GetToeicTestDetailBoundary.Response res = getToeicTestDetail.execute(
                GetToeicTestDetailBoundary.Request.builder()
                        .userId(userId)
                        .testId(id)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<ToeicTestSummaryDto> createTestMultipart(
            @CurrentUserId String userId,
            @RequestPart(value = "requestData") String requestDataJson,
            @RequestPart(value = "pdfFile", required = false) MultipartFile pdfFile,
            @RequestPart(value = "audioFile", required = false) MultipartFile audioFile) {

        try {
            CreateToeicTestRequest data = objectMapper.readValue(requestDataJson, CreateToeicTestRequest.class);
            CreateToeicTestBoundary.Response res = createToeicTest.execute(
                    CreateToeicTestBoundary.Request.builder()
                            .userId(userId)
                            .requestData(data)
                            .pdfFile(pdfFile)
                            .audioFile(audioFile)
                            .build());
            return ResponseEntity.ok(res.getData());
        } catch (Exception e) {
            log.error("Failed to parse create test request: {}", e.getMessage());
            throw new RuntimeException("Dữ liệu không hợp lệ: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<ToeicTestSummaryDto> createTestJson(
            @CurrentUserId String userId,
            @Valid @RequestBody CreateToeicTestRequest data) {

        CreateToeicTestBoundary.Response res = createToeicTest.execute(
                CreateToeicTestBoundary.Request.builder()
                        .userId(userId)
                        .requestData(data)
                        .pdfFile(null)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<ToeicTestSummaryDto> updateTestMultipart(
            @CurrentUserId String userId,
            @PathVariable String id,
            @RequestPart(value = "requestData", required = false) String requestDataJson,
            @RequestPart(value = "pdfFile", required = false) MultipartFile pdfFile,
            @RequestPart(value = "audioFile", required = false) MultipartFile audioFile) {

        try {
            UpdateToeicTestRequest data = (requestDataJson != null && !requestDataJson.trim().isEmpty()) ?
                    objectMapper.readValue(requestDataJson, UpdateToeicTestRequest.class) : new UpdateToeicTestRequest();

            UpdateToeicTestBoundary.Response res = updateToeicTest.execute(
                    UpdateToeicTestBoundary.Request.builder()
                            .userId(userId)
                            .testId(id)
                            .requestData(data)
                            .pdfFile(pdfFile)
                            .audioFile(audioFile)
                            .build());
            return ResponseEntity.ok(res.getData());
        } catch (Exception e) {
            log.error("Failed to parse update test request: {}", e.getMessage());
            throw new RuntimeException("Dữ liệu không hợp lệ: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<ToeicTestSummaryDto> updateTestJson(
            @CurrentUserId String userId,
            @PathVariable String id,
            @Valid @RequestBody UpdateToeicTestRequest data) {

        UpdateToeicTestBoundary.Response res = updateToeicTest.execute(
                UpdateToeicTestBoundary.Request.builder()
                        .userId(userId)
                        .testId(id)
                        .requestData(data)
                        .pdfFile(null)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<java.util.Map<String, Object>> deleteTest(
            @CurrentUserId String userId,
            @PathVariable String id) {

        DeleteToeicTestBoundary.Response res = deleteToeicTest.execute(
                DeleteToeicTestBoundary.Request.builder()
                        .userId(userId)
                        .testId(id)
                        .build());

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("success", res.isSuccess());
        body.put("message", res.getMessage());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize(AppAuthorities.HAS_CARD_WRITE)
    public ResponseEntity<SubmitToeicSessionResponse> submitTest(
            @CurrentUserId String userId,
            @PathVariable String id,
            @Valid @RequestBody SubmitToeicSessionRequest submission) {

        SubmitToeicSessionBoundary.Response res = submitToeicSession.execute(
                SubmitToeicSessionBoundary.Request.builder()
                        .userId(userId)
                        .testId(id)
                        .submissionData(submission)
                        .build());
        return ResponseEntity.ok(res.getData());
    }

    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> streamPdfFile(@PathVariable String filename) {
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads", "toeic_pdfs", filename);
            if (!java.nio.file.Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error reading PDF file: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/proxy-pdf")
    public ResponseEntity<org.springframework.core.io.Resource> proxyPdf(@RequestParam("url") String urlStr) {
        try {
            if (urlStr.contains("res.cloudinary.com") && urlStr.contains("/image/upload/")) {
                urlStr = urlStr.replace("/image/upload/", "/raw/upload/");
            }
            java.net.URI uri = java.net.URI.create(urlStr);
            org.springframework.core.io.Resource remoteResource = new org.springframework.core.io.UrlResource(uri);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document.pdf\"")
                    .body(remoteResource);
        } catch (Exception e) {
            log.error("Error proxying PDF: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/audio/file/{filename:.+}")
    public void streamAudioFile(
            @PathVariable String filename,
            @RequestHeader(value = org.springframework.http.HttpHeaders.RANGE, required = false) String rangeHeader,
            jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads", "toeic_audio", filename);
            if (!java.nio.file.Files.exists(filePath)) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }
            long fileLength = java.nio.file.Files.size(filePath);
            if (fileLength <= 0) {
                response.setStatus(HttpStatus.OK.value());
                return;
            }
            String contentType = resolveAudioContentType(filename);

            long start = 0;
            long end = fileLength - 1;
            int status = HttpStatus.OK.value();

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                List<org.springframework.http.HttpRange> ranges = org.springframework.http.HttpRange.parseRanges(rangeHeader);
                if (!ranges.isEmpty()) {
                    org.springframework.http.HttpRange range = ranges.get(0);
                    start = range.getRangeStart(fileLength);
                    end = range.getRangeEnd(fileLength);
                    status = HttpStatus.PARTIAL_CONTENT.value();
                }
            }

            response.setStatus(status);
            response.setContentType(contentType);
            response.setHeader(org.springframework.http.HttpHeaders.ACCEPT_RANGES, "bytes");
            response.setHeader(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + filename + "\"");
            if (status == HttpStatus.PARTIAL_CONTENT.value()) {
                response.setHeader(org.springframework.http.HttpHeaders.CONTENT_RANGE,
                        "bytes " + start + "-" + end + "/" + fileLength);
            }
            long count = end - start + 1;
            response.setHeader(org.springframework.http.HttpHeaders.CONTENT_LENGTH, Long.toString(count));

            try (java.io.InputStream is = java.nio.file.Files.newInputStream(filePath);
                 java.io.OutputStream out = response.getOutputStream()) {
                long skip = start;
                while (skip > 0) {
                    long skipped = is.skip(skip);
                    if (skipped <= 0) break;
                    skip -= skipped;
                }
                long remaining = count;
                byte[] buf = new byte[8192];
                int n;
                while (remaining > 0 && (n = is.read(buf, 0, (int) Math.min(buf.length, remaining))) > 0) {
                    out.write(buf, 0, n);
                    remaining -= n;
                }
            }
        } catch (Exception e) {
            log.error("Error reading audio file: {}", e.getMessage());
            if (!response.isCommitted()) {
                response.resetBuffer();
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
    }

    @GetMapping("/proxy-audio")
    public ResponseEntity<org.springframework.core.io.Resource> proxyAudio(@RequestParam("url") String urlStr) {
        try {
            java.net.URI uri = java.net.URI.create(urlStr);
            org.springframework.core.io.Resource remoteResource = new org.springframework.core.io.UrlResource(uri);
            org.springframework.http.MediaType contentType = MediaType.parseMediaType(
                    resolveAudioContentType(urlStr));
            return ResponseEntity.ok()
                    .contentType(contentType)
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(remoteResource);
        } catch (Exception e) {
            log.error("Error proxying audio: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private String resolveAudioContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".m4a")) return "audio/mp4";
        if (lower.endsWith(".wav")) return "audio/wav";
        if (lower.endsWith(".ogg")) return "audio/ogg";
        if (lower.endsWith(".webm")) return "audio/webm";
        if (lower.endsWith(".aac")) return "audio/aac";
        if (lower.endsWith(".flac")) return "audio/flac";
        if (lower.endsWith(".opus")) return "audio/ogg";
        return "audio/mpeg"; // mp3 & default
    }
}
