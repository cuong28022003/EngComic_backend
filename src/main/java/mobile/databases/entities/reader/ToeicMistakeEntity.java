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

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "toeic_mistakes")
public class ToeicMistakeEntity {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String testId;
    private String testName;
    private String attemptId;
    private int questionNumber;
    private int part;
    private String userAnswer;
    private String correctAnswer;
    private String explanation;
    
    @Builder.Default
    private String reason = "wrong"; // "wrong" | "flagged"

    @Builder.Default
    private String status = "pending"; // "pending" | "explained" | "resolved"
    
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
