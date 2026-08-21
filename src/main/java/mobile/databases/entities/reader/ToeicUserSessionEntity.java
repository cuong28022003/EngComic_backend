package mobile.databases.entities.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "toeic_user_sessions")
public class ToeicUserSessionEntity {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String testId;
    private String testName;
    
    private int rawScore;
    private Integer scaledScore;
    private int totalQuestions;
    private int duration; // in seconds

    private String timeMode; // "full_test", "per_part", "untimed"
    private int part5TargetSeconds;
    private int part6TargetSeconds;
    private int part7TargetSeconds;
    private int part5ElapsedSeconds;
    private int part6ElapsedSeconds;
    private int part7ElapsedSeconds;
    
    @Builder.Default
    private List<Integer> selectedParts = new ArrayList<>();

    @Builder.Default
    private List<UserAnswerRecord> answers = new ArrayList<>();
    
    @CreatedDate
    private Date submittedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserAnswerRecord {
        private int questionNumber;
        private int part;
        private String userAnswer;
        private String correctAnswer;
        private boolean isCorrect;
        private boolean flagged;
        private int timeSpentSeconds;
    }
}
