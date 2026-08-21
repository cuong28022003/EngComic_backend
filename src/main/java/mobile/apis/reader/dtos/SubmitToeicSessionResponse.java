package mobile.apis.reader.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitToeicSessionResponse {
    private String testId;
    private String testName;
    private String attemptId;
    private int attemptNumber;
    private int rawScore;
    private Integer scaledScore;
    private int totalQuestions;
    private double accuracyPercentage;
    private int duration;
    private List<PartBreakdownDto> partBreakdown;
    private List<GradedQuestionDto> results;
    private List<ToeicMistakeDto> newMistakes;
}
