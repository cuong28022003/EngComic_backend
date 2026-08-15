package mobile.model.Entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import java.util.Map;

@Data
@Document(collection = "character_animations")
public class CharacterAnimation {
    @Id
    private String id;

    private String characterId;

    /**
     * key: actionCode (vd: "0", "200")
     * value: AnimationAction
     */
    private Map<String, AnimationAction> actions;

}
