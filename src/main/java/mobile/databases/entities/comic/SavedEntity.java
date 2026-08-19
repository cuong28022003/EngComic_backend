package mobile.databases.entities.comic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "saved")
public class SavedEntity {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String comicId;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public SavedEntity(String userId, String comicId) {
        this.userId = userId;
        this.comicId = comicId;
        this.createdAt = LocalDateTime.now();
    }
}

