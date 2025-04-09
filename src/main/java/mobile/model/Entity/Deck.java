package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported=false)
@Document(collection = "deck")
public class Deck {
    @Id
    protected ObjectId id;
    protected ObjectId userId;
    protected String name;
    protected String description;
    @CreatedDate
    protected Date createAt;
    @LastModifiedDate
    protected Date updateAt;
}
