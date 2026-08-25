package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.businesses.boundaries.reader.DeleteToeicTestBoundary;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.entities.reader.ToeicTestEntity;
import mobile.databases.repositories.reader.ToeicMistakeRepository;
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
    private final ToeicMistakeRepository mistakeRepository;

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

        // 1. Delete associated attempts
        List<ToeicTestAttemptEntity> attempts = attemptRepository.findByUserIdAndTestIdOrderByStartedAtDesc(userId, testId);
        if (!attempts.isEmpty()) {
            attemptRepository.deleteAll(attempts);
        }

        // 2. Delete local PDF file if exists
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
