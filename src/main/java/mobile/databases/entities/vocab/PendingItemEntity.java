package mobile.databases.entities.vocab;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pending_item")
public class PendingItemEntity {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String content;
    private String sourceType;
    private String sourceCardId;
    private String status = "pending";

    @CreatedDate
    private Date createdAt;
}
