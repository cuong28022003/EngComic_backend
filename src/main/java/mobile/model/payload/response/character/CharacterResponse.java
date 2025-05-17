package mobile.model.payload.response.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.Entity.Pack;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    private ObjectId id;
    private String name;
    private String rarity;
    private String imageUrl;
    private String description;
    private Pack pack;
}
