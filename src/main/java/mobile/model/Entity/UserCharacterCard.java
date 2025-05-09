package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported = false)
@Document(collection = "user_card")
public class UserCharacterCard {
    @Id
    private ObjectId id;
    private ObjectId userId;
    private ObjectId cardId;
    private Date obtainedAt;
}
