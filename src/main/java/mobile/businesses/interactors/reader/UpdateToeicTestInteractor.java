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

        if (data != null && data.getSection() != null && !data.getSection().trim().isEmpty()) {
            test.setSection(data.getSection().trim());
        }

        // Handle PDF file upload if provided
        if (request.getPdfFile() != null && !request.getPdfFile().isEmpty()) {
            String oldLocalPath = test.getLocalPdfPath();
            String oldPdfUrl = test.getPdfUrl();

            try {
                java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads", "toeic_pdfs");
                if (!java.nio.file.Files.exists(uploadDir)) {
                    java.nio.file.Files.createDirectories(uploadDir);
                }
                String originalName = request.getPdfFile().getOriginalFilename();
                String ext = (originalName != null && originalName.contains(".")) ?
                        originalName.substring(originalName.lastIndexOf(".")) : ".pdf";
                String localFilename = java.util.UUID.randomUUID().toString() + ext;
                java.nio.file.Path targetPath = uploadDir.resolve(localFilename);
                java.nio.file.Files.copy(request.getPdfFile().getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                test.setLocalPdfPath(localFilename);
                test.setPdfUrl("/api/toeic/tests/file/" + localFilename);
                log.info("Saved TOEIC PDF directly to local storage on update: {}", test.getPdfUrl());

                // Clean up old local file if existed
                if (oldLocalPath != null && !oldLocalPath.isEmpty()) {
                    try {
                        java.nio.file.Path oldPath = uploadDir.resolve(oldLocalPath);
                        if (java.nio.file.Files.exists(oldPath)) {
                            java.nio.file.Files.delete(oldPath);
                        }
                    } catch (Exception ex) {
                        log.warn("Could not delete old local PDF file: {}", ex.getMessage());
                    }
                }
                // Clean up old cloudinary file if it was on cloudinary
                if (oldPdfUrl != null && oldPdfUrl.contains("res.cloudinary.com")) {
                    try {
                        cloudinaryService.deleteFile(oldPdfUrl);
                    } catch (Exception ex) {
                        log.warn("Could not delete previous Cloudinary PDF: {}", ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to save local PDF on update: {}", ex.getMessage());
                throw new RuntimeException("Không thể lưu trữ tệp PDF: " + ex.getMessage());
            }
        } else if (data != null && data.getPdfUrl() != null && !data.getPdfUrl().trim().isEmpty()) {
            test.setPdfUrl(data.getPdfUrl().trim());
        }

        // Handle audio file upload if provided (TOEIC Listening)
        if (request.getAudioFile() != null && !request.getAudioFile().isEmpty()) {
            String oldAudioPath = test.getLocalAudioPath();

            try {
                java.nio.file.Path audioDir = java.nio.file.Paths.get("uploads", "toeic_audio");
                if (!java.nio.file.Files.exists(audioDir)) {
                    java.nio.file.Files.createDirectories(audioDir);
                }
                String originalName = request.getAudioFile().getOriginalFilename();
                String ext = (originalName != null && originalName.contains(".")) ?
                        originalName.substring(originalName.lastIndexOf(".")) : ".mp3";
                String localAudioFilename = java.util.UUID.randomUUID().toString() + ext;
                java.nio.file.Path targetPath = audioDir.resolve(localAudioFilename);
                java.nio.file.Files.copy(request.getAudioFile().getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                test.setLocalAudioPath(localAudioFilename);
                test.setAudioUrl("/api/toeic/tests/audio/file/" + localAudioFilename);
                log.info("Saved TOEIC audio on update: {}", test.getAudioUrl());

                if (oldAudioPath != null && !oldAudioPath.isEmpty()) {
                    try {
                        java.nio.file.Path oldPath = audioDir.resolve(oldAudioPath);
                        if (java.nio.file.Files.exists(oldPath)) {
                            java.nio.file.Files.delete(oldPath);
                        }
                    } catch (Exception ex) {
                        log.warn("Could not delete old local audio file: {}", ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to save local audio on update: {}", ex.getMessage());
                throw new RuntimeException("Không thể lưu trữ tệp audio: " + ex.getMessage());
            }
        }

        // Handle questions update if provided
        if (data != null && data.getQuestions() != null && !data.getQuestions().isEmpty()) {
            List<ToeicQuestion> questions = data.getQuestions().stream()
                    .map(q -> ToeicQuestion.builder()
                            .number(q.getNumber())
                            .part(q.getPart())
                            .correctAnswer(q.getCorrectAnswer() != null ? q.getCorrectAnswer().trim().toUpperCase() : "")
                            .audioStartMs(q.getAudioStartMs())
                            .transcript(q.getTranscript())
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
