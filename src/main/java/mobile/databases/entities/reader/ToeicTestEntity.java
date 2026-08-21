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
@Document(collection = "toeic_tests")
public class ToeicTestEntity {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String testName;
    private String pdfUrl;
    private String localPdfPath;
    
    private Integer rawScore;
    private Integer scaledScore;
    
    @Builder.Default
    private String status = "not_started"; // "not_started" | "in_progress" | "completed"
    
    @Builder.Default
    private List<ToeicQuestion> questions = new ArrayList<>();
    
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
