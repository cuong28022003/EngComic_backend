package mobile.apis.reader.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradedQuestionDto {
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
