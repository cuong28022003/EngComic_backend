package mobile.databases.entities.pendingitem;

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
    private String content;         // "decisive" or "reach a decision"
    private String sourceType;      // "family" | "collocation" | "synonym" | "manual"
    private String sourceCardId;    // which card spawned this suggestion (nullable)
    private String status = "pending"; // "pending" | "imported"

    @CreatedDate
    private Date createdAt;
}
