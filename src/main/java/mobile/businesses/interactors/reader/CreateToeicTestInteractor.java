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

                pdfUrl = "/api/toeic/tests/file/" + localFilename;
                log.info("Saved TOEIC PDF directly to local storage: {}", pdfUrl);
            } catch (Exception ex) {
                log.error("Failed to save local PDF: {}", ex.getMessage());
                throw new RuntimeException("Không thể lưu trữ tệp PDF: " + ex.getMessage());
            }
        }

        String audioUrl = null;
        String localAudioFilename = null;
        if (request.getAudioFile() != null && !request.getAudioFile().isEmpty()) {
            try {
                java.nio.file.Path audioDir = java.nio.file.Paths.get("uploads", "toeic_audio");
                if (!java.nio.file.Files.exists(audioDir)) {
                    java.nio.file.Files.createDirectories(audioDir);
                }
                String originalName = request.getAudioFile().getOriginalFilename();
                String ext = (originalName != null && originalName.contains(".")) ?
                        originalName.substring(originalName.lastIndexOf(".")) : ".mp3";
                localAudioFilename = java.util.UUID.randomUUID().toString() + ext;
                java.nio.file.Path targetPath = audioDir.resolve(localAudioFilename);
                java.nio.file.Files.copy(request.getAudioFile().getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                audioUrl = "/api/toeic/tests/audio/file/" + localAudioFilename;
                log.info("Saved TOEIC audio directly to local storage: {}", audioUrl);
            } catch (Exception ex) {
                log.error("Failed to save local audio: {}", ex.getMessage());
                throw new RuntimeException("Không thể lưu trữ tệp audio: " + ex.getMessage());
            }
        }

List<ToeicQuestion> questions = new ArrayList<>();
        if (data != null && data.getQuestions() != null) {
            questions = data.getQuestions().stream()
                    .map(q -> ToeicQuestion.builder()
                            .number(q.getNumber())
                            .part(q.getPart())
                            .correctAnswer(q.getCorrectAnswer() != null ? q.getCorrectAnswer().trim().toUpperCase() : "")
                            .audioStartMs(q.getAudioStartMs())
                            .transcript(q.getTranscript())
                            .build())
                    .collect(Collectors.toList());
        }

        String testName = (data != null && data.getTestName() != null) ? data.getTestName().trim() : "TOEIC Test";
        String section = (data != null && data.getSection() != null && !data.getSection().trim().isEmpty())
                ? data.getSection().trim() : "reading";

        ToeicTestEntity entity = ToeicTestEntity.builder()
                .userId(request.getUserId())
                .testName(testName)
                .section(section)
                .pdfUrl(pdfUrl)
                .localPdfPath(localFilename)
                .audioUrl(audioUrl)
                .localAudioPath(localAudioFilename)
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
