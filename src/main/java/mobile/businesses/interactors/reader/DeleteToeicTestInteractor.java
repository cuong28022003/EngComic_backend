package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.businesses.boundaries.reader.DeleteToeicTestBoundary;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.entities.reader.ToeicTestEntity;
import mobile.databases.repositories.reader.ToeicReviewItemRepository;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import mobile.databases.repositories.reader.ToeicTestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteToeicTestInteractor implements DeleteToeicTestBoundary {

    private final ToeicTestRepository testRepository;
    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReviewItemRepository reviewItemRepository;
    private final mobile.databases.services.CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public Response execute(Request request) {
        String userId = request.getUserId();
        String testId = request.getTestId();

        Optional<ToeicTestEntity> testOpt = testRepository.findByIdAndUserId(testId, userId);
        if (testOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đề thi hoặc bạn không có quyền xóa");
        }

        ToeicTestEntity test = testOpt.get();

        // 1. Delete associated attempts & reviews
        List<ToeicTestAttemptEntity> attempts = attemptRepository.findByUserIdAndTestIdOrderByStartedAtDesc(userId, testId);
        if (!attempts.isEmpty()) {
            attemptRepository.deleteAll(attempts);
        }
        reviewItemRepository.deleteByTestId(testId);

        // 2. Delete Cloudinary PDF or local PDF file if exists
        if (test.getPdfUrl() != null && test.getPdfUrl().contains("res.cloudinary.com")) {
            try {
                cloudinaryService.deleteFile(test.getPdfUrl());
            } catch (Exception e) {
                log.warn("Could not delete Cloudinary PDF {}: {}", test.getPdfUrl(), e.getMessage());
            }
        }
        if (test.getLocalPdfPath() != null && !test.getLocalPdfPath().isEmpty()) {
            try {
                Path filePath = Paths.get("uploads", "toeic_pdfs", test.getLocalPdfPath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (Exception e) {
                log.warn("Could not delete local PDF file {}: {}", test.getLocalPdfPath(), e.getMessage());
            }
        }

        // 3. Delete the test itself
        testRepository.delete(test);

        return Response.builder()
                .success(true)
                .message("Đã xóa đề thi thành công")
                .build();
    }
}
