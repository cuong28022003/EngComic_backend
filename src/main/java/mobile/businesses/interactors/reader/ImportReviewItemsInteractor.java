package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.ImportReviewItemsRequest;
import mobile.businesses.boundaries.reader.ImportReviewItemsBoundary;
import mobile.databases.entities.reader.ToeicMistakeEntity;
import mobile.databases.entities.reader.ToeicReviewItemEntity;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.repositories.reader.ToeicMistakeRepository;
import mobile.databases.repositories.reader.ToeicReviewItemRepository;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportReviewItemsInteractor implements ImportReviewItemsBoundary {

    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReviewItemRepository reviewItemRepository;
    private final ToeicMistakeRepository mistakeRepository;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        ToeicTestAttemptEntity attempt = attemptRepository.findByIdAndUserId(request.getAttemptId(), request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài"));

        ImportReviewItemsRequest importData = request.getImportData();
        if (importData == null || importData.getItems() == null || importData.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu JSON phân tích không được để trống");
        }

        List<ToeicReviewItemEntity> entitiesToSave = new ArrayList<>();
        List<Integer> importedQuestionNumbers = new ArrayList<>();

        for (ImportReviewItemsRequest.ReviewItemInput item : importData.getItems()) {
            int qNum = item.getQuestionNumber();
            importedQuestionNumbers.add(qNum);

            int part = item.getPart() != null ? item.getPart() : inferPart(qNum);

            ToeicReviewItemEntity reviewEntity = reviewItemRepository
                    .findByUserIdAndAttemptIdAndQuestionNumber(request.getUserId(), attempt.getId(), qNum)
                    .orElse(ToeicReviewItemEntity.builder()
                            .userId(request.getUserId())
                            .testId(attempt.getTestId())
                            .attemptId(attempt.getId())
                            .questionNumber(qNum)
                            .part(part)
                            .createdAt(new Date())
                            .build());

            reviewEntity.setPart(part);
            reviewEntity.setErrorType(item.getErrorType() != null ? item.getErrorType().trim().toLowerCase() : "vocab");
            reviewEntity.setErrorSubtype(item.getErrorSubtype());
            reviewEntity.setRelatedGrammarTopic(item.getRelatedGrammarTopic());
            reviewEntity.setPassageExcerpt(item.getPassageExcerpt());
            reviewEntity.setQuestionText(item.getQuestionText());
            reviewEntity.setOptions(item.getOptions() != null ? item.getOptions() : new HashMap<>());
            reviewEntity.setExplanation(item.getExplanation());
            reviewEntity.setTip(item.getTip());
            reviewEntity.setUpdatedAt(new Date());

            if (item.getKeyVocab() != null) {
                List<ToeicReviewItemEntity.KeyVocabItem> vocabList = item.getKeyVocab().stream()
                        .map(v -> ToeicReviewItemEntity.KeyVocabItem.builder()
                                .word(v.getWord())
                                .meaningVi(v.getMeaningVi())
                                .build())
                        .collect(Collectors.toList());
                reviewEntity.setKeyVocab(vocabList);
            }

            entitiesToSave.add(reviewEntity);
        }

        List<ToeicReviewItemEntity> savedReviews = reviewItemRepository.saveAll(entitiesToSave);

        // Update corresponding mistake entities to status = 'explained'
        try {
            List<ToeicMistakeEntity> userMistakes = mistakeRepository.findByUserIdOrderByCreatedAtDesc(request.getUserId());
            for (ToeicMistakeEntity mistake : userMistakes) {
                if (importedQuestionNumbers.contains(mistake.getQuestionNumber()) &&
                        (attempt.getId().equals(mistake.getAttemptId()) || attempt.getTestId().equals(mistake.getTestId()))) {
                    if ("pending".equalsIgnoreCase(mistake.getStatus())) {
                        mistake.setStatus("explained");
                        // Find explanation from saved reviews
                        savedReviews.stream()
                                .filter(r -> r.getQuestionNumber() == mistake.getQuestionNumber())
                                .findFirst()
                                .ifPresent(r -> mistake.setExplanation(r.getExplanation()));
                        mistake.setUpdatedAt(new Date());
                        mistakeRepository.save(mistake);
                    }
                }
            }
        } catch (Exception e) {
            // Non-blocking log
        }

        return Response.builder()
                .data(savedReviews.stream().map(mapper::toReviewItemDto).collect(Collectors.toList()))
                .build();
    }

    private int inferPart(int qNum) {
        if (qNum >= 101 && qNum <= 130) return 5;
        if (qNum >= 131 && qNum <= 146) return 6;
        if (qNum >= 147 && qNum <= 200) return 7;
        return 5;
    }
}
