package mobile.apis.vocab.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitLevelAnswerRequest {
    @Min(0)
    @Max(5)
    @JsonProperty("quality")
    private int quality;             // 0-5 (0-2: Wrong, 3-5: Correct)
    
    @JsonProperty("isCorrect")
    @JsonAlias({"is_correct", "correct"})
    private boolean isCorrect;       // explicit boolean
    
    @JsonProperty("currentLevel")
    @JsonAlias({"current_level", "level", "masteryLevel"})
    private int currentLevel;        // 1 to 4
    
    @JsonProperty("confidenceScore")
    @JsonAlias({"confidence_score", "confidence"})
    private Integer confidenceScore; // 1 to 5 (for Level 4)
    
    @JsonProperty("answerText")
    @JsonAlias({"answer_text", "answer"})
    private String answerText;       // optional submitted text
}
