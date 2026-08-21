package mobile.databases.entities.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
@Document(collection = "toeic_test_attempts")
public class ToeicTestAttemptEntity {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String testId;
    private String testName;
    private int attemptNumber;

    @Builder.Default
    private String status = "in_progress"; // "in_progress", "completed", "abandoned"

    private String timeMode; // "full_test", "per_part", "untimed"

    @Builder.Default
    private List<Integer> selectedParts = new ArrayList<>();

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

    @Builder.Default
    private List<UserAnswerRecord> answers = new ArrayList<>();

    @CreatedDate
    private Date startedAt;

    @LastModifiedDate
    private Date lastSavedAt;

    private Date completedAt;

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
