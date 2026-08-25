package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.reader.dtos.ToeicTestSummaryDto;
import mobile.apis.reader.dtos.UpdateToeicTestRequest;
import mobile.businesses.boundaries.reader.UpdateToeicTestBoundary;
import mobile.databases.entities.reader.ToeicQuestion;
import mobile.databases.entities.reader.ToeicTestEntity;
import mobile.databases.repositories.reader.ToeicTestRepository;
import mobile.databases.services.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateToeicTestInteractor implements UpdateToeicTestBoundary {

    private final ToeicTestRepository testRepository;
    private final CloudinaryService cloudinaryService;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        String userId = request.getUserId();
        String testId = request.getTestId();

        Optional<ToeicTestEntity> testOpt = testRepository.findByIdAndUserId(testId, userId);
        if (testOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đề thi hoặc bạn không có quyền chỉnh sửa");
        }

        ToeicTestEntity test = testOpt.get();
        UpdateToeicTestRequest data = request.getRequestData();

        if (data != null && data.getTestName() != null && !data.getTestName().trim().isEmpty()) {
            test.setTestName(data.getTestName().trim());
        }

        // Handle PDF file upload if provided
        if (request.getPdfFile() != null && !request.getPdfFile().isEmpty()) {
            String localFilename = null;
            try {
                java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads", "toeic_pdfs");
                if (!java.nio.file.Files.exists(uploadDir)) {
                    java.nio.file.Files.createDirectories(uploadDir);
                }
                String originalName = request.getPdfFile().getOriginalFilename();
                String ext = (originalName != null && originalName.contains(".")) ?
                        originalName.substring(originalName.lastIndexOf(".")) : ".pdf";
                localFilename = java.util.UUID.randomUUID().toString() + ext;
                java.nio.file.Path targetPath = uploadDir.resolve(localFilename);
                java.nio.file.Files.copy(request.getPdfFile().getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                log.error("Failed to save local PDF on update: {}", e.getMessage());
            }

            if (localFilename != null) {
                test.setLocalPdfPath(localFilename);
                test.setPdfUrl("/api/toeic/tests/file/" + localFilename);
            } else {
                try {
                    String uploadedUrl = cloudinaryService.uploadFile(request.getPdfFile(), "toeic_pdfs");
                    test.setPdfUrl(uploadedUrl);
                } catch (IOException e) {
                    log.warn("Failed to upload PDF to Cloudinary on update: {}", e.getMessage());
                }
            }
        } else if (data != null && data.getPdfUrl() != null && !data.getPdfUrl().trim().isEmpty()) {
            test.setPdfUrl(data.getPdfUrl().trim());
        }

        // Handle questions update if provided
        if (data != null && data.getQuestions() != null && !data.getQuestions().isEmpty()) {
            List<ToeicQuestion> questions = data.getQuestions().stream()
                    .map(q -> ToeicQuestion.builder()
                            .number(q.getNumber())
                            .part(q.getPart())
                            .correctAnswer(q.getCorrectAnswer() != null ? q.getCorrectAnswer().trim().toUpperCase() : "")
                            .build())
                    .collect(Collectors.toList());
            test.setQuestions(questions);
        }

        test.setUpdatedAt(new Date());
        ToeicTestEntity saved = testRepository.save(test);

        return Response.builder()
                .data(mapper.toSummaryDto(saved))
                .build();
    }
}
