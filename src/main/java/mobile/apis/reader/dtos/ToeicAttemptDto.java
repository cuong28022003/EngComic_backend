package mobile.apis.reader.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToeicAttemptDto {
    private String id;
    private String testId;
    private String testName;
    private int attemptNumber;
    private String status; // "in_progress", "completed", "abandoned"
    private String timeMode;
    private List<Integer> selectedParts;
    private int part5TargetSeconds;
    private int part6TargetSeconds;
    private int part7TargetSeconds;
    private int totalElapsedSeconds;
    private int part5ElapsedSeconds;
    private int part6ElapsedSeconds;
    private int part7ElapsedSeconds;
    private int rawScore;
    private Integer scaledScore;
    private int totalQuestions;
    private double accuracyPercentage;
    private List<ToeicTestAttemptAnswerDto> answers;
    private Date startedAt;
    private Date lastSavedAt;
    private Date completedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToeicTestAttemptAnswerDto {
        private int questionNumber;
        private int part;
        private String userAnswer;
        private String correctAnswer;

        @JsonProperty("isCorrect")
        private boolean isCorrect;
        private boolean flagged;
        private int timeSpentSeconds;

        @JsonProperty("isCorrect")
        public boolean isCorrect() {
            return isCorrect;
        }

        @JsonProperty("isCorrect")
        public void setCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }
}
