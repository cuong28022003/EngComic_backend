package mobile.apis.reader.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToeicMistakeDto {
    private String id;
    private String testId;
    private String testName;
    private String attemptId;
    private int questionNumber;
    private int part;
    private String userAnswer;
    private String correctAnswer;
    private String explanation;
    private String reason; // "wrong" | "flagged"
    private String status; // "pending" | "explained" | "resolved"
    private Date createdAt;
    private Date updatedAt;
}
