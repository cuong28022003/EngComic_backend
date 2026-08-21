package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.reader.dtos.CreateToeicTestRequest;
import mobile.businesses.boundaries.reader.CreateToeicTestBoundary;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateToeicTestInteractor implements CreateToeicTestBoundary {

    private final ToeicTestRepository testRepository;
    private final CloudinaryService cloudinaryService;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        CreateToeicTestRequest data = request.getRequestData();
        String pdfUrl = data != null ? data.getPdfUrl() : null;

        String localFilename = null;
        if (request.getPdfFile() != null && !request.getPdfFile().isEmpty()) {
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
                log.error("Failed to save local PDF: {}", e.getMessage());
            }

            if (localFilename != null) {
                // Use local backend streaming endpoint to avoid browser tracking prevention / CDN block
                pdfUrl = "/api/toeic/tests/file/" + localFilename;
            } else {
                try {
                    pdfUrl = cloudinaryService.uploadFile(request.getPdfFile(), "toeic_pdfs");
                } catch (IOException e) {
                    log.warn("Failed to upload PDF to Cloudinary: {}", e.getMessage());
                }
            }
        }

        List<ToeicQuestion> questions = new ArrayList<>();
        if (data != null && data.getQuestions() != null) {
            questions = data.getQuestions().stream()
                    .map(q -> ToeicQuestion.builder()
                            .number(q.getNumber())
                            .part(q.getPart())
                            .correctAnswer(q.getCorrectAnswer() != null ? q.getCorrectAnswer().trim().toUpperCase() : "")
                            .build())
                    .collect(Collectors.toList());
        }

        String testName = (data != null && data.getTestName() != null) ? data.getTestName().trim() : "TOEIC Test";

        ToeicTestEntity entity = ToeicTestEntity.builder()
                .userId(request.getUserId())
                .testName(testName)
                .pdfUrl(pdfUrl)
                .localPdfPath(localFilename)
                .status("not_started")
                .questions(questions)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        ToeicTestEntity saved = testRepository.save(entity);
        return Response.builder()
                .data(mapper.toSummaryDto(saved))
                .build();
    }
}
