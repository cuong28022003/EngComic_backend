package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import org.springframework.data.annotation.Id;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported=false)
@Document(collection = "character")
public class Character {
    @Id
    private String id;
    private String name;
    private String rarity; // C, R, SR, SSR
    private String imageUrl;
    private String description;
    private String packId; // ID của gói thẻ mà thẻ này thuộc về
    private int bonusXp;
    private int bonusDiamond;

    private Map<String, Integer> skillsUsagePerDay; // {"DOUBLE_XP": 1, "SHOW_ANSWER": 2}

    private String version; //SEASON_1,..
    private String type; // "ENEMY", "ALLY"

    private String transformationCharacterId; // ID của nhân vật biến hình
}
