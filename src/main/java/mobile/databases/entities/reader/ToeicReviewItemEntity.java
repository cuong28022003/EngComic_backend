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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "toeic_review_items")
public class ToeicReviewItemEntity {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String testId;
    private String attemptId;
    private int questionNumber;
    private int part;

    private String errorType; // "vocab", "grammar", "inference", "detail_missed", "trap_answer", "time_pressure"
    private String errorSubtype;
    private String passageExcerpt;
    private String questionText;

    @Builder.Default
    private Map<String, String> options = new HashMap<>(); // A, B, C, D

    private String explanation;
    private String tip;

    @Builder.Default
    private List<KeyVocabItem> keyVocab = new ArrayList<>();

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KeyVocabItem {
        private String word;
        private String meaningVi;
    }
}
