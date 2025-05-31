package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.TopupService;
import mobile.model.Entity.Topup;
import mobile.model.payload.request.topup.TopupRequest;
import mobile.model.payload.response.topup.TopupResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topup")
@RequiredArgsConstructor
public class TopupController {
    private final TopupService topupService;

    @PostMapping("")
    public ResponseEntity<Topup> createTopup(@RequestBody TopupRequest topupRequest, HttpServletRequest request) {
        return ResponseEntity.ok(topupService.save(topupRequest));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TopupResponse>> getTopupHistory(@RequestParam String userId, HttpServletRequest request) {
        List<Topup> history = topupService.findByUserId(new ObjectId(userId));
        List<TopupResponse> response = history.stream()
                .map(topup -> new TopupResponse(
                        topup.getId().toString(),
                        topup.getUserId().toString(),
                        topup.getDiamond(),
                        topup.getNote(),
                        topup.getCreatedAt(),
                        topup.isProcessed(),
                        topup.isCanceled())).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{topupId}/cancel")
    public ResponseEntity<?> cancelTopup(@PathVariable String topupId, HttpServletRequest request) {
        topupService.cancelTopup(new ObjectId(topupId));
        return ResponseEntity.ok("Đã hủy yêu cầu nạp.");
    }

    @GetMapping
    public ResponseEntity<Page<TopupResponse>> getAllTopups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Topup> topups = topupService.findAll(pageable);
        Page<TopupResponse> response = topups.map(topup -> new TopupResponse(
                topup.getId().toString(),
                topup.getUserId().toString(),
                topup.getDiamond(),
                topup.getNote(),
                topup.getCreatedAt(),
                topup.isProcessed(),
                topup.isCanceled()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{topupId}/confirm")
    public ResponseEntity<TopupResponse> confirmTopup(@PathVariable String topupId, HttpServletRequest request) {
        Topup topup = topupService.confirmTopup(new ObjectId(topupId));
        return ResponseEntity.ok(new TopupResponse(
                topup.getId().toString(),
                topup.getUserId().toString(),
                topup.getDiamond(),
                topup.getNote(),
                topup.getCreatedAt(),
                topup.isProcessed(),
                topup.isCanceled()));
    }

}
