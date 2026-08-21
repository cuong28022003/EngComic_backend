package mobile.businesses.interactors.reader;

import mobile.apis.reader.dtos.ToeicMistakeDto;
import mobile.apis.reader.dtos.ToeicTestDetailDto;
import mobile.apis.reader.dtos.ToeicTestSummaryDto;
import mobile.databases.entities.reader.ToeicMistakeEntity;
import mobile.databases.entities.reader.ToeicTestEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ToeicReaderMapper {

    public ToeicTestSummaryDto toSummaryDto(ToeicTestEntity entity) {
        if (entity == null) return null;
        return ToeicTestSummaryDto.builder()
                .id(entity.getId())
                .testName(entity.getTestName())
                .pdfUrl(entity.getPdfUrl())
                .questionCount(entity.getQuestions() != null ? entity.getQuestions().size() : 0)
                .rawScore(entity.getRawScore())
                .scaledScore(entity.getScaledScore())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ToeicTestDetailDto toDetailDto(ToeicTestEntity entity) {
        if (entity == null) return null;
        return ToeicTestDetailDto.builder()
                .id(entity.getId())
                .testName(entity.getTestName())
                .pdfUrl(entity.getPdfUrl())
                .status(entity.getStatus())
                .rawScore(entity.getRawScore())
                .scaledScore(entity.getScaledScore())
                .questions(entity.getQuestions() != null ?
                        entity.getQuestions().stream()
                                .map(q -> ToeicTestDetailDto.QuestionDetailItem.builder()
                                        .number(q.getNumber())
                                        .part(q.getPart())
                                        .build())
                                .collect(Collectors.toList()) : Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ToeicMistakeDto toMistakeDto(ToeicMistakeEntity entity) {
        if (entity == null) return null;
        return ToeicMistakeDto.builder()
                .id(entity.getId())
                .testId(entity.getTestId())
                .testName(entity.getTestName())
                .attemptId(entity.getAttemptId())
                .questionNumber(entity.getQuestionNumber())
                .part(entity.getPart())
                .userAnswer(entity.getUserAnswer())
                .correctAnswer(entity.getCorrectAnswer())
                .explanation(entity.getExplanation())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public mobile.apis.reader.dtos.ToeicReviewItemDto toReviewItemDto(mobile.databases.entities.reader.ToeicReviewItemEntity entity) {
        if (entity == null) return null;
        return mobile.apis.reader.dtos.ToeicReviewItemDto.builder()
                .id(entity.getId())
                .testId(entity.getTestId())
                .attemptId(entity.getAttemptId())
                .questionNumber(entity.getQuestionNumber())
                .part(entity.getPart())
                .errorType(entity.getErrorType())
                .errorSubtype(entity.getErrorSubtype())
                .passageExcerpt(entity.getPassageExcerpt())
                .questionText(entity.getQuestionText())
                .options(entity.getOptions())
                .explanation(entity.getExplanation())
                .tip(entity.getTip())
                .keyVocab(entity.getKeyVocab() != null ?
                        entity.getKeyVocab().stream()
                                .map(v -> mobile.apis.reader.dtos.ToeicReviewItemDto.KeyVocabDto.builder()
                                        .word(v.getWord())
                                        .meaningVi(v.getMeaningVi())
                                        .build())
                                .collect(Collectors.toList()) : Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public mobile.apis.reader.dtos.ToeicAttemptDto toAttemptDto(mobile.databases.entities.reader.ToeicTestAttemptEntity entity) {
        if (entity == null) return null;
        double acc = entity.getTotalQuestions() > 0 ? ((double) entity.getRawScore() / entity.getTotalQuestions()) * 100.0 : 0.0;
        return mobile.apis.reader.dtos.ToeicAttemptDto.builder()
                .id(entity.getId())
                .testId(entity.getTestId())
                .testName(entity.getTestName())
                .attemptNumber(entity.getAttemptNumber())
                .status(entity.getStatus())
                .timeMode(entity.getTimeMode())
                .selectedParts(entity.getSelectedParts())
                .part5TargetSeconds(entity.getPart5TargetSeconds())
                .part6TargetSeconds(entity.getPart6TargetSeconds())
                .part7TargetSeconds(entity.getPart7TargetSeconds())
                .totalElapsedSeconds(entity.getTotalElapsedSeconds())
                .part5ElapsedSeconds(entity.getPart5ElapsedSeconds())
                .part6ElapsedSeconds(entity.getPart6ElapsedSeconds())
                .part7ElapsedSeconds(entity.getPart7ElapsedSeconds())
                .rawScore(entity.getRawScore())
                .scaledScore(entity.getScaledScore())
                .totalQuestions(entity.getTotalQuestions())
                .accuracyPercentage(Math.round(acc * 10.0) / 10.0)
                .answers(entity.getAnswers() != null ?
                        entity.getAnswers().stream()
                                .map(a -> mobile.apis.reader.dtos.ToeicAttemptDto.ToeicTestAttemptAnswerDto.builder()
                                        .questionNumber(a.getQuestionNumber())
                                        .part(a.getPart())
                                        .userAnswer(a.getUserAnswer())
                                        .correctAnswer(a.getCorrectAnswer())
                                        .isCorrect(a.isCorrect())
                                        .flagged(a.isFlagged())
                                        .timeSpentSeconds(a.getTimeSpentSeconds())
                                        .build())
                                .collect(Collectors.toList()) : Collections.emptyList())
                .startedAt(entity.getStartedAt())
                .lastSavedAt(entity.getLastSavedAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }
}
