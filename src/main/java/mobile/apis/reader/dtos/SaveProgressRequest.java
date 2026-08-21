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
public class SaveProgressRequest {
    private int totalElapsedSeconds;
    private int part5ElapsedSeconds;
    private int part6ElapsedSeconds;
    private int part7ElapsedSeconds;
    private List<AnswerItem> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerItem {
        private int questionNumber;
        private int part;
        private String answer;
        private boolean flagged;
        private int timeSpentSeconds;
    }
}
