package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import java.util.Map;

@Data
@Document(collection = "character_sprites")
@AllArgsConstructor
@NoArgsConstructor
public class CharacterSprite {
    @Id
    private String id;
    private String characterId;
    /**
     * key: actionCode (vd: "0", "200")
     * value: Map<frameIndex, SpriteFrame>
     */
    private Map<String, Map<String, SpriteFrame>> groups;
}
