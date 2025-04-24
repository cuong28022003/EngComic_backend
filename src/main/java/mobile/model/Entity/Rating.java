package mobile.model.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Date;

@Getter
@Setter
@RestResource(exported=false)
@Document(collection = "rating")
public class Rating {
    @Id
    protected ObjectId id;
    protected ObjectId userId;
    protected ObjectId comicId;
    protected int rating;
    protected String comment;
    @CreatedDate
    protected Date createdAt;
    @LastModifiedDate
    protected Date updatedAt;
}
