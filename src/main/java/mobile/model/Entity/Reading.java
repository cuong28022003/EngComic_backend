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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported=false)
@Document(collection = "reading")
public class Reading {
    @Id
    protected ObjectId id;
	protected ObjectId userId;
    protected ObjectId comicId;
    protected int chapterNumber;
}