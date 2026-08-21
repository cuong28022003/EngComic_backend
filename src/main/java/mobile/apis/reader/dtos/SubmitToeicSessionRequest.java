package mobile.apis.reader.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitToeicSessionRequest {
    private String attemptId;
    private int duration; // in seconds
    private String timeMode; // "full_test", "per_part", "untimed"
    private List<Integer> selectedParts; // e.g. [5], [6], [7], or [5, 6, 7]
    private int part5TargetSeconds;
    private int part6TargetSeconds;
    private int part7TargetSeconds;
    private int part5ElapsedSeconds;
    private int part6ElapsedSeconds;
    private int part7ElapsedSeconds;

    @NotEmpty(message = "Danh sách câu trả lời không được để trống")
    private List<UserAnswerItem> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnswerItem {
        private int questionNumber;
        private String answer; // "A", "B", "C", "D" or null/empty
        private boolean flagged;
        private int timeSpentSeconds;
    }
}
