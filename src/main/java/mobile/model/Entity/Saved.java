package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;

@Getter
@Setter
@RestResource(exported=false)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "saved")
public class Saved {
    @Id
    private ObjectId id;
    private ObjectId userId;
    private ObjectId comicId;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Saved(ObjectId userId, ObjectId comicId) {
        this.userId = userId;
        this.comicId = comicId;
        this.createdAt = LocalDateTime.now();
    }
}
