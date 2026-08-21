package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.ImportReviewItemsRequest;
import mobile.businesses.boundaries.reader.ImportMistakeReviewsBoundary;
import mobile.databases.entities.reader.ToeicMistakeEntity;
import mobile.databases.entities.reader.ToeicReviewItemEntity;
import mobile.databases.repositories.reader.ToeicMistakeRepository;
import mobile.databases.repositories.reader.ToeicReviewItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportMistakeReviewsInteractor implements ImportMistakeReviewsBoundary {

    private final ToeicMistakeRepository mistakeRepository;
    private final ToeicReviewItemRepository reviewItemRepository;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        ImportReviewItemsRequest importData = request.getImportData();
        if (importData == null || importData.getItems() == null || importData.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu JSON không được để trống");
        }

        List<ToeicMistakeEntity> userMistakes = mistakeRepository.findByUserIdOrderByCreatedAtDesc(request.getUserId());
        List<ToeicMistakeEntity> updatedMistakes = new ArrayList<>();
        List<ToeicReviewItemEntity> reviewEntitiesToSave = new ArrayList<>();

        for (ImportReviewItemsRequest.ReviewItemInput item : importData.getItems()) {
            int qNum = item.getQuestionNumber();
            int part = item.getPart() != null ? item.getPart() : inferPart(qNum);

            // Find matching mistake
            ToeicMistakeEntity matchedMistake = userMistakes.stream()
                    .filter(m -> m.getQuestionNumber() == qNum)
                    .findFirst()
                    .orElse(null);

            if (matchedMistake != null) {
                matchedMistake.setStatus("explained");
                matchedMistake.setExplanation(item.getExplanation());
                matchedMistake.setUpdatedAt(new Date());
                updatedMistakes.add(matchedMistake);

                // Also save/update ToeicReviewItemEntity
                String testId = matchedMistake.getTestId();
                String attemptId = matchedMistake.getAttemptId();

                ToeicReviewItemEntity reviewEntity = null;
                if (attemptId != null) {
                    reviewEntity = reviewItemRepository.findByUserIdAndAttemptIdAndQuestionNumber(request.getUserId(), attemptId, qNum).orElse(null);
                }
                if (reviewEntity == null) {
                    reviewEntity = ToeicReviewItemEntity.builder()
                            .userId(request.getUserId())
                            .testId(testId)
                            .attemptId(attemptId)
                            .questionNumber(qNum)
                            .part(part)
                            .createdAt(new Date())
                            .build();
                }

                reviewEntity.setPart(part);
                reviewEntity.setErrorType(item.getErrorType() != null ? item.getErrorType().trim().toLowerCase() : "vocab");
                reviewEntity.setErrorSubtype(item.getErrorSubtype());
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

                reviewEntitiesToSave.add(reviewEntity);
            }
        }

        if (!updatedMistakes.isEmpty()) {
            mistakeRepository.saveAll(updatedMistakes);
        }
        if (!reviewEntitiesToSave.isEmpty()) {
            reviewItemRepository.saveAll(reviewEntitiesToSave);
        }

        return Response.builder()
                .data(updatedMistakes.stream().map(mapper::toMistakeDto).collect(Collectors.toList()))
                .build();
    }

    private int inferPart(int qNum) {
        if (qNum >= 101 && qNum <= 130) return 5;
        if (qNum >= 131 && qNum <= 146) return 6;
        if (qNum >= 147 && qNum <= 200) return 7;
        return 5;
    }
}
