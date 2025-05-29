package mobile.model.Entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Date;

@Getter
@Setter
@RestResource(exported = false)
@Document(collection = "report")
public class Report {
    @Id
    protected ObjectId id;

    protected ObjectId comicId;

    protected ObjectId userId;

    protected String reason;

    protected String status;

    @CreatedDate
    protected Date createdAt;

    @LastModifiedDate
    protected Date updatedAt;

}
